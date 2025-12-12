import { createApp } from 'vue'
import {
    Building2,
    Mail,
    Lock,
    Phone,
    MapPin,
    AlertCircle,
    Heart
} from 'lucide-vue-next'

export function registerLucideIcons(app) {
    app.component('Building2Icon', Building2)
    app.component('MailIcon', Mail)
    app.component('LockIcon', Lock)
    app.component('PhoneIcon', Phone)
    app.component('MapPinIcon', MapPin)
    app.component('AlertCircleIcon', AlertCircle)
    app.component('HeartIcon', Heart)
}