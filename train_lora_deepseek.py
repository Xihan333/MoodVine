import torch
from transformers import (
    AutoModelForCausalLM,
    AutoTokenizer,
    TrainingArguments,
    Trainer,
    DataCollatorForLanguageModeling
)
from peft import LoraConfig, get_peft_model
import datasets

model_name = "deepseek-ai/deepseek-llm-7b-chat"
tokenizer = AutoTokenizer.from_pretrained(model_name, trust_remote_code=True)
tokenizer.pad_token = tokenizer.eos_token  # 设置pad token

model = AutoModelForCausalLM.from_pretrained(
    model_name,
    torch_dtype=torch.bfloat16,
    device_map="auto",
    trust_remote_code=True
)


# 数据预处理
def format_conversation(messages):
    formatted = []
    for msg in messages:
        if msg["role"] == "user":
            formatted.append(f"User: {msg['content']}")
        else:
            formatted.append(f"Assistant: {msg['content']}")
    return "\n\n".join(formatted) + tokenizer.eos_token


def preprocess_function(examples):
    texts = [format_conversation(m) for m in examples["messages"]]

    tokenized = tokenizer(
        texts,
        truncation=True,
        max_length=1024,
        padding="max_length",
        return_tensors="pt"
    )

    tokenized["labels"] = tokenized["input_ids"].clone()
    return tokenized


dataset = datasets.load_dataset("json", data_files="SoulChatCorpus-sft-multi-Turn")["train"]
tokenized_dataset = dataset.map(
    preprocess_function,
    batched=True,
    remove_columns=["messages", "id", "topic"]
)

# 配置LoRA
lora_config = LoraConfig(
    r=8,
    lora_alpha=32,
    target_modules=["q_proj", "v_proj"],
    lora_dropout=0.05,
    bias="none",
    task_type="CAUSAL_LM"
)
model = get_peft_model(model, lora_config)
model.print_trainable_parameters()

# 训练配置
training_args = TrainingArguments(
    output_dir="./output",
    per_device_train_batch_size=2,  # 7B模型需要更小的batch
    gradient_accumulation_steps=16,  # 补偿batch_size
    num_train_epochs=3,
    learning_rate=2e-5,
    weight_decay=0.01,
    fp16=True,
    logging_steps=50,
    save_strategy="steps",
    save_steps=500,
    evaluation_strategy="no",
    optim="adamw_torch",
    max_grad_norm=0.3,
    warmup_ratio=0.03,
    lr_scheduler_type="cosine"
)

# 数据整理器
data_collator = DataCollatorForLanguageModeling(
    tokenizer=tokenizer,
    mlm=False
)

# 训练器
trainer = Trainer(
    model=model,
    args=training_args,
    train_dataset=tokenized_dataset,
    data_collator=data_collator,
)

# 训练
trainer.train()

# 保存
model.save_pretrained("./output/lora_weights")
tokenizer.save_pretrained("./output")