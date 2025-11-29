import { useQuery } from "@tanstack/react-query"
import { categoryAPI } from "../api/categoryAPI"

export const useCategory = () => {

  
  // 전체 카테고리 트리 조회
  const getCategoryList = () => {
    return useQuery({
      queryKey: ["categoryList"],
      queryFn: () => categoryAPI.get(),
    });
  };

    return {
        getCategoryList
    };
}
