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
          className="d-block w-100"
          accept='image/jpg,impge/png,image/jpeg,image/gif' 
          src="https://cdn.pixabay.com/photo/2020/09/02/20/52/dock-5539524__340.jpg"
          alt="First slide"
        />
        <Carousel.Caption
            style = {{
                marginBottom: '150px'
            }}
        >
          <h3>First slide label</h3>
          <p>Nulla vitae elit libero, a pharetra augue mollis interdum.</p>
        </Carousel.Caption>
      </Carousel.Item>

      <Carousel.Item>
        <img
          className="d-block w-100"
          src="https://cdn.pixabay.com/photo/2021/02/03/13/54/cupcake-5978060__340.jpg"
          alt="Second slide"
        />

        <Carousel.Caption
          style = {{
            marginBottom: '150px'
        }}
        >
          <h3>Second slide label</h3>
          <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</p>
        </Carousel.Caption>
      </Carousel.Item>

      <Carousel.Item>
        <img
          className="d-block w-100"
          src="https://cdn.pixabay.com/photo/2020/08/04/14/42/sky-5463015__340.jpg"
          alt="Third slide"
        />

        <Carousel.Caption
          style = {{
            marginBottom: '150px'
        }}>
          <h3>Third slide label</h3>
          <p>
            Praesent commodo cursus magna, vel scelerisque nisl consectetur.
          </p>
        </Carousel.Caption>
      </Carousel.Item>
    </Carousel>
  );
};

export default ControlledCarousel;
