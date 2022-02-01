// import React from 'react';
// import './Slider.css'
// import dataSlider from './dataSlider';
// import BtnSlider from './BtnSlider';

// const Slider = () => {
//   const [slideIndex, setSlideIndex] = useState(1);
//   const nextSlide = () =>{

//   }

//   return (
//     <div className = "container-slider">
//       {dataSlider.map((obj, index) => {
//         return(
//           <div
//             key = {obj.id}
//             className = {slideIndex === index + 1}
//           >
//             <img src={process.env.PUBLIC_URL + `/Imgs/img${index + 1}.jpg`}/>
//           </div>
//         )
//       })}
//       <BtnSlider moveSlide = {nextSlide} direction = {"next"}/>
//       <BtnSlider movieSlide = {prevSlide} direction = {"prev"}/>
//     </div>
//   );
// };

// export default Slider;