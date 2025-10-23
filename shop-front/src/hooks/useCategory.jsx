import { useQuery } from "@tanstack/react-query"
import { categoryAPI } from "../api/categoryAPI"

export const useCategory = () => {

    const getCategoryList = () => {
        return useQuery({
            queryKey: ["category"],
            queryFn: () => categoryAPI.get(),
        });
    };

    return {
        getCategoryList
    }
}
