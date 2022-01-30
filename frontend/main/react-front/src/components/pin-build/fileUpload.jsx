import React from 'react';
import './fileUpload.scss';
import { BsFillArrowUpCircleFill } from "react-icons/bs";

const fileUpload = ({ formpage, files, saveFileImage }) => {
  
  const handleSaveFileImage = (e) =>{
    console.log(e.target.files)
    saveFileImage(e.target.files);
  }

  return (
      <>   
          <input
            onChange={handleSaveFileImage}
            type="file"
            id="file"
            accept="image/*"
            name="file"
            multiple
          />
          {files && (   
            <div className="file-img-container">
              <img src={files} alt="sample" />
            </div>
          )
            }
          {
            !files && (
              <div className ="drag-text">
                <BsFillArrowUpCircleFill size="30"></BsFillArrowUpCircleFill>
                <p>드래그하거나 클릭하여 업로드</p>
             </div>
          )
          }
      </>
  );
};

export default fileUpload;