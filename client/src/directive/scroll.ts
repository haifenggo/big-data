import { App } from 'vue';
export default function (app: App<Element>) {
	app.directive('scroll', {
		mounted(el) {
			let timer: number;
			//el就是绑定的dom对象
			if (!el) return;
			const scroll = () => {
				let parentDom = el;
				// 判断是否有滚动条
				if (parentDom.scrollHeight <= parentDom.clientHeight) {
					timer = requestAnimationFrame(scroll);
					return;
				}
				//判断元素是否滚动到底部(可视高度+距离顶部=整个高度)
				if (parentDom.scrollTop + parentDom.clientHeight + 1 >= parentDom.scrollHeight) {
					parentDom.scrollTop = 0;
					timer = requestAnimationFrame(scroll);
				} else {
					parentDom.scrollTop++; // 元素自增距离顶部
					timer = requestAnimationFrame(scroll);
				}
			};
			timer = requestAnimationFrame(scroll);
			el.onmouseenter = () => {
				cancelAnimationFrame(timer);
			};
			el.onmouseleave = () => {
				timer = requestAnimationFrame(scroll);
			};
		},
	});
}
