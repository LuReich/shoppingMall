export const categoryAPI = {

    //카테고리 조회
    get:  async() => {
        const res = await api.get("/category/list");
        return res.data;
    }
}