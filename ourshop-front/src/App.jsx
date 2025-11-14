import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { router } from './router/router';
import { RouterProvider } from 'react-router'
import './App.css';

function App() {

  //react-query 설정
  const queryClient = new QueryClient({
    defaultOptions:{
    queries: {
      retry: 1, //응답이 없으면 1번 다시 시도
      staleTime: 1*60*1000, //1분, 얼마나 refresh 기간을 짧게 줄거냐
      gcTime: 1*60*1000, //1분, 컴포넌트가 사라졌다면 그 이후에도 얼마나 데이터를 유지할 것이냐
      refetchOnWindowFocus: true, //포커스를 다시 받았을 떄 재실행 여부

    }
  }
});


  return (
    <>
      <QueryClientProvider client={queryClient}>
        <RouterProvider router={router}/>
      </QueryClientProvider> 
    </>
  )
}

export default App
