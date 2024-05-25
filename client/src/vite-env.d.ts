/// <reference types="vite/client" />

interface ImportMetaEnv {
	readonly VITE_APP_TITLE: string;
	readonly VITE_PROXY_TARGET: string;
	readonly VITE_PORT: number;
	readonly VITE_BASE_URL: string;
	readonly VITE_WEBSOCKET_URL: string;
}

interface ImportMeta {
	readonly env: ImportMetaEnv;
}
