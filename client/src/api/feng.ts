import axios from 'axios';
import { da } from 'element-plus/es/locales.mjs';
const instance = axios.create({
	baseURL: '/feng',
	timeout: 1000,
});

const getWebSocketList = async () => {
	const { data } = await instance.get('/webSocketList');
	return data;
};

const startLocationCount = async () => {
	const { data } = await instance.get('/startLocationCount');
	return data;
};
const startSentimentTrend = async () => {
	const { data } = await instance.get('/startSentimentTrend');
	return data;
};
const startLikesTrend = async () => {
	const { data } = await instance.get('/startLikesTrend');
	return data;
};

export { getWebSocketList, startLikesTrend, startLocationCount, startSentimentTrend };
