import "slick-carousel/slick/slick.css"; // slick 기본 CSS
import "slick-carousel/slick/slick-theme.css"; // slick 테마 CSS
import '../../assets/css/MainBanner.css';
import React, { useState, useRef } from 'react';
import Slider from 'react-slick';
import { FaPlay } from "react-icons/fa6";
import { TbPlayerPauseFilled } from "react-icons/tb";
import { useNavigate } from "react-router";


function NextArrow(props) {
    const { onClick } = props;
    return <button className="slick-arrow-custom next-btn" onClick={onClick} />;
}
function PrevArrow(props) {
    const { onClick } = props;
    return <button className="slick-arrow-custom prev-btn" onClick={onClick} />;
}

const MainBanner = () => {

    const navigate = useNavigate();
    const sliderRef = useRef(null);
    const [isPaused, setIsPaused] = useState(false);
    const [currentSlide, setCurrentSlide] = useState(0);

    const slides = [
        { id: 1, className: 'slide-1', title: '우리샵 GRAND OPEN!', desc: '판매자와 구매자가 서로를 믿고 거래하는 공간, 우리샵에서 비즈니스의 새로운 가능성을 시작하세요.', url: '#/' },
        { id: 2, className: 'slide-2', title: '나만의 맞춤 추천', desc: '당신이 좋아한 상품을 통해 신뢰할 수 있는 파트너를 찾아드립니다.', url: '/shop/recommend' },
        { id: 3, className: 'slide-3', title: '판매인증 신청하고', desc: '더 많은 구매자와의 거래 기회를 잡으세요!', url: '/verification'  }
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
                    <div key={slide.id} className={`slide ${slide.className}`} onClick={() => navigate(slide.url)}>
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