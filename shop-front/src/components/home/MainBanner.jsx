import "slick-carousel/slick/slick.css"; // slick 기본 CSS
import "slick-carousel/slick/slick-theme.css"; // slick 테마 CSS
import '../../assets/css/MainBanner.css';
import React, { useState, useRef } from 'react';
import Slider from 'react-slick';
import { FaPlay } from "react-icons/fa6";
import { TbPlayerPauseFilled } from "react-icons/tb";


function NextArrow(props) {
    const { onClick } = props;
    return <button className="slick-arrow-custom next-btn" onClick={onClick} />;
}
function PrevArrow(props) {
    const { onClick } = props;
    return <button className="slick-arrow-custom prev-btn" onClick={onClick} />;
}

const MainBanner = () => {
    const sliderRef = useRef(null);
    const [isPaused, setIsPaused] = useState(false);
    const [currentSlide, setCurrentSlide] = useState(0);

    const slides = [
        { id: 1, className: 'slide-1', title: '우리샵 GRAND OPEN!', desc: '내 취향에 맞는 업체, 지금 바로 추천받으세요.' },
        { id: 2, className: 'slide-2', title: '이번 주 특가 상품', desc: '최대 30% 할인 혜택을 놓치지 마세요!' },
        { id: 3, className: 'slide-3', title: '정기배송 신청하고', desc: '매주 신선함을 문 앞에서 받아보세요.' }
    ];

    const togglePlayPause = () => {
        if (isPaused) sliderRef.current.slickPlay();
        else sliderRef.current.slickPause();
        setIsPaused(!isPaused);
    };

    const goToSlide = (index) => {
        sliderRef.current.slickGoTo(index);
    }

    const settings = {
        dots: false,
        infinite: true,
        speed: 500,
        slidesToShow: 1,
        slidesToScroll: 1,
        autoplay: true,
        autoplaySpeed: 3000,
        arrows: true,
        draggable: false,
        swipe: false,
        nextArrow: <NextArrow />,
        prevArrow: <PrevArrow />,
        beforeChange: (old, next) => setCurrentSlide(next),
    };

    return (
        <div className="main-banner">
            <Slider ref={sliderRef} {...settings}>
                {slides.map(slide => (
                    <div key={slide.id} className={`slide ${slide.className}`}>
                        <h3>{slide.title}</h3>
                        <p>{slide.desc}</p>
                    </div>
                ))}
            </Slider>

            <div className="dots-container">
                {/*  Dot 그룹의 완벽한 중앙 정렬을 위한 가상 버튼  */}
                <button className="play-pause-btn dummy">
                    {isPaused ? <FaPlay/> : <TbPlayerPauseFilled/>}
                </button>

                <div className="dots-wrapper">
                    {slides.map((slide, index) => (
                        <span
                            key={slide.id}
                            className={`dot ${currentSlide === index ? 'active' : ''}`}
                            onClick={() => goToSlide(index)}
                        />
                    ))}
                </div>

                <button className="play-pause-btn" onClick={togglePlayPause}>
                    {isPaused ? <FaPlay/> : '❚❚'}
                </button>
            </div>
        </div>
    );
};

export default MainBanner;