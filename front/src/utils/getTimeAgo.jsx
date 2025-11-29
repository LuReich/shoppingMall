import dayjs from "dayjs";
import relativeTime from "dayjs/plugin/relativeTime";
import "dayjs/locale/ko";

dayjs.extend(relativeTime);
dayjs.locale("ko");

export function getTimeAgo(date) {
  return dayjs(date).fromNow();  // → "3일 전", "2시간 전", "방금 전"
}