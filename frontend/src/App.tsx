import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import ProductsListPage from './pages/ProductsListPage'
import ProductDetailPage from './pages/ProductDetailPage'
import './styles.css'


const qc = new QueryClient()


export default function App(){
  return (
    <QueryClientProvider client={qc}>
    <BrowserRouter>
    <Routes>
    <Route path="/" element={<ProductsListPage/>}/>
    <Route path="/products/:id" element={<ProductDetailPage/>}/>
    </Routes>
    </BrowserRouter>
    </QueryClientProvider>
  )
}