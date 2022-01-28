import React, {useState} from "react";
import {Carousel} from "react-bootstrap";

const ControlledCarousel = () => {
  const [index, setIndex] = useState(0);

  const handleSelect = (selectedIndex, e) => {
    setIndex(selectedIndex);
  };

  return (
    <Carousel activeIndex={index} onSelect={handleSelect}>
      <Carousel.Item>
        <img
          className="d-block w-80"
          accept='image/jpg,impge/png,image/jpeg,image/gif' 
          src=""
          alt="First slide"
        />
        <Carousel.Caption
            style = {{
                marginBottom: '150px'
            }}
        >
        </Carousel.Caption>
      </Carousel.Item>
      <Carousel.Item>
        <img
          className="d-block w-80"
          src=""
          alt="Second slide"
        />
        <Carousel.Caption
          style = {{
            marginBottom: '150px'
        }}
        >
        </Carousel.Caption>
      </Carousel.Item>

      <Carousel.Item>
        <img
          className="d-block w-80"
          src=""
          alt="Third slide"
        />
        <Carousel.Caption
          style = {{
            marginBottom: '150px'
        }}>
        </Carousel.Caption>
      </Carousel.Item>
    </Carousel>
  );
};

export default ControlledCarousel;
