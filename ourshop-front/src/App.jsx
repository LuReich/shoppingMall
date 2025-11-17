import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { router } from './router/router';
import { RouterProvider } from 'react-router'
import './App.css';

function App() {

  // react-query 설정
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: 1,
        staleTime: 1 * 60 * 1000,
        gcTime: 1 * 60 * 1000,
        refetchOnWindowFocus: true,
      }
    }
  });

  return (
    // 🟦 전체 1700px 고정 레이아웃
    <div className="layout-wrapper">
      <QueryClientProvider client={queryClient}>
        <RouterProvider router={router}/>
      </QueryClientProvider>
    </div>
  )
}

export default App;
