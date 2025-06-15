<template>
    <div class="main">
        <el-button type="primary" @click="showAddDialog">新增活动</el-button>
        <el-table :data="activities">
            <el-table-column prop="name" label="活动名称" min-width="40"></el-table-column>
            <el-table-column prop="description" label="活动内容"></el-table-column>
            <el-table-column prop="startTime" label="开始时间" min-width="40"></el-table-column>
            <el-table-column prop="finishTime" label="结束时间" min-width="40"></el-table-column>
            <el-table-column prop="number" label="参与人数" min-width="40"></el-table-column>
            <el-table-column label="操作">
                <template #default="scope">
                <el-button size="small" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                <el-button size="small" type="danger" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-dialog v-model="dialogVisible" :title="dialogTitle">
        <el-form :model="form" label-width="120px">
            <el-form-item label="活动名称">
            <el-input v-model="form.name"></el-input>
            </el-form-item>
            <el-form-item label="活动内容">
            <el-input v-model="form.description" type="textarea"></el-input>
            </el-form-item>
            <el-form-item label="开始时间">
            <el-date-picker v-model="form.startTime" type="date" placeholder="选择日期"></el-date-picker>
            </el-form-item>
            <el-form-item label="结束时间">
            <el-date-picker v-model="form.finishTime" type="date" placeholder="选择日期"></el-date-picker>
            </el-form-item>
            <el-form-item label="活动图片">
                <div>
                    <input type="file" @change="handleFileUpload" accept="image/*" />
                    <div v-if="form.picture">
                        <img :src="form.picture" alt="预览图片" style="max-width: 300px; max-height: 300px;" />
                    </div>
                </div>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="confirmDialog">确认</el-button>
        </template>
        </el-dialog>
    </div>
</template>

<style scoped>
.main{
    width: 100%;
}
</style>

<script setup>
import { ref } from 'vue';
import request from '../request/axios_config.js'
import { onMounted } from 'vue';
import { ElMessage } from 'element-plus'

onMounted(() => {
    const fetchData = async () => {
        const res = await request.get('/admin/activity/getAllActivities');
        if(res.data.code===200){
            activities.value=res.data.data.activities;
        }
    }
    fetchData();
})
const selectedFile = ref(null)
const handleFileUpload = (e) => {
    const file = e.target.files[0];
    if (file) {
        selectedFile.value = file;
        form.value.picture = URL.createObjectURL(file);
    } 
}

const activities = ref([]);
const dialogVisible = ref(false);
const dialogTitle = ref('新增活动');
const form = ref({
    id: 0,
    name: '',
    description: '',
    picture: '',
    startTime: '',
    finishTime: '',
    number: 0
});

const showAddDialog = () => {
    dialogTitle.value = '新增活动';
    form.value = {
        id: 0,
        name: '',
        description: '',
        picture: '',
        startTime: '',
        finishTime: '',
        number: 0
    };
    dialogVisible.value = true;
};

const handleEdit = (index, row) => {
    dialogTitle.value = '编辑活动';
    form.value = { ...row };
    dialogVisible.value = true;
};

// 删除
const handleDelete = async (index, row) => {
    const res = await request.post('/admin/activity/delete', {
        id: row.id
    });
    if(res.data.code===200){
        activities.value = activities.value.filter(item => item.id !== row.id);
        ElMessage({
            message: '删除成功',
            type: 'success',
        })
    }
};

// 新增或编辑
const confirmDialog = async () => {
    if (dialogTitle.value === '新增活动') {
        if(selectedFile.value){
            const formData = new FormData();
            formData.append('file', selectedFile.value);
            const res = await request.post('/file/upload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
            if(res.data.code===200){
                selectedFile.value=''
                const startTime=`${form.value.startTime.getFullYear()}-${String(form.value.startTime.getMonth() + 1).padStart(2, '0')}-${String(form.value.startTime.getDate()).padStart(2, '0')}`
                const finishTime=`${form.value.finishTime.getFullYear()}-${String(form.value.finishTime.getMonth() + 1).padStart(2, '0')}-${String(form.value.finishTime.getDate()).padStart(2, '0')}`
                // 添加活动
                const res2 = await request.post('/admin/activity/add',{
                    name: form.value.name,
                    description: form.value.description,
                    picture: res.data.msg,
                    startTime: startTime,
                    finishTime: finishTime,
                });
                if(res2.data.code===200){
                    // 更新数据
                    form.value.id=res2.data.data.activity
                    form.value.startTime=startTime
                    form.value.finishTime=finishTime
                    form.value.picture=res.data.msg
                    activities.value.push(form.value);
                    dialogVisible.value = false;
                    ElMessage({
                        message: '新增成功',
                        type: 'success',
                    })
                }
            }
        }
    } else {
        // 编辑功能
        if(selectedFile.value){
            const formData = new FormData();
            formData.append('file', selectedFile.value);
            const res = await request.post('/file/upload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
            if(res.data.code===200){
                selectedFile.value=''
                form.value.picture=res.data.msg;
            }
        }

        let startTime=form.value.startTime;
        if(startTime instanceof Date){
            startTime=`${startTime.getFullYear()}-${String(startTime.getMonth() + 1).padStart(2, '0')}-${String(startTime.getDate()).padStart(2, '0')}`
        }
        let finishTime=form.value.finishTime;
        if(finishTime instanceof Date){
            finishTime=`${finishTime.getFullYear()}-${String(finishTime.getMonth() + 1).padStart(2, '0')}-${String(finishTime.getDate()).padStart(2, '0')}`
        }
        // 添加活动
        const res2 = await request.post('/admin/activity/edit',{
            id: form.value.id,
            name: form.value.name,
            description: form.value.description,
            picture: form.value.picture,
            startTime: startTime,
            finishTime: finishTime,
        });
        if(res2.data.code===200){
            form.value.startTime=startTime
            form.value.finishTime=finishTime
            activities.value = activities.value.map(activity => {
                if (activity.id === form.value.id) {
                    return { ...form.value }; // 返回一个新的对象，包含 form 的数据
                }
                return activity; // 否则返回原始对象
            });
            dialogVisible.value = false;
            ElMessage({
                message: '编辑成功',
                type: 'success',
            })
        }
    }
};

    
</script>