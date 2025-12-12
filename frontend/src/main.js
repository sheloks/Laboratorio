import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'  // ← CORREGIDO (era ../App.vue)
import router from './router'  // ← Esto está bien
import './style.css'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

app.mount('#app')