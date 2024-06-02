// 创建一个映射对象，将英文字段映射到中文字段
const translationMap: any = {
	comprehensive: '综合热榜',
	national_original: '国创相关',
	anime: '动漫',
	music: '音乐',
	dancing: '舞蹈',
	games: '游戏',
	knowledge: '知识',
	technology: '科技',
	sports: '运动',
	fashion: '时尚',
	fun: '娱乐',
	movies: '影视',
	origin: '原创',
	rookie: '新人',
};

// 定义一个函数来转换英文字段到中文字段
export function translateToChinese(englishTerm: string) {
	return translationMap[englishTerm] || englishTerm;
}
