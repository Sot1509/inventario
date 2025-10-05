import axios from 'axios'


export const http = axios.create({
    headers: {
        'Accept': 'application/vnd.api+json',
        'Content-Type': 'application/vnd.api+json',
        'X-API-KEY': import.meta.env.VITE_API_KEY || 'secret123'
    }
})