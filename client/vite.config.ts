import { defineConfig, loadEnv } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';
import AutoImport from 'unplugin-auto-import/vite';
import Component from 'unplugin-vue-components/vite';
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers';
// https://vitejs.dev/config/

export default defineConfig(({ command, mode }) => {
	console.log(command, mode);
	const env = loadEnv(mode, process.cwd(), '');
	let config = {
		plugins: [
			vue(),
			AutoImport({
				resolvers: [ElementPlusResolver()],
			}),
			Component({
				resolvers: [ElementPlusResolver()],
			}),
		],
		resolve: {
			alias: {
				'@': path.resolve(__dirname, './src'),
			},
		},
	};
	if (mode == 'development') {
		const server = {
			proxy: {
				'/feng': {
					target: env.VITE_BASE_URL,
					changeOrigin: true,
					rewrite: (path: any) => path.replace(/^\/feng/, ''),
				},
			},
		};
		return { server, ...config };
	}

	return config;
});
