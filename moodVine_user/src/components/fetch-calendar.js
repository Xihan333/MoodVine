export async function getGitHubContributions() {
	const url = `https://github.com/users/Xihan333/contributions`;
	const html = await (await fetch(url)).text();

	const tooltips = html.matchAll(/>(\w+) contributions? on \w+ \w+/g);
	const cells = html.matchAll(/data-date="(\d+-\d+-\d+)" .*? data-level="(\d+)"/g);
	let contributions = [];

	for (const [, count] of tooltips) {
		let [, date, level] = cells.next().value;
		contributions.push({
			level: parseInt(level),
			date: new Date(date).getTime(),
			count: parseInt(count) || 0,
		});
	}
	return contributions.sort((a, b) => a.date - b.date);
}
