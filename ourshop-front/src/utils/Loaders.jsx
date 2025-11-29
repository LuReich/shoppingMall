import { AiOutlineLoading3Quarters } from "react-icons/ai";
import "../assets/css/loader.css";

export default function Loader() {
  return (
    <div className="loader-center">
      <AiOutlineLoading3Quarters className="spin" size={50} color="#7cc7de" />
    </div>
  );
}
