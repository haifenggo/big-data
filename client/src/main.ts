import { createApp } from 'vue';
import './style.css';
import ScrollDirective from './directive/scroll';
import App from './App.vue';
import '@/assets/reset.css';
const app = createApp(App);
ScrollDirective(app);
app.mount('#app');
