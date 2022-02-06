import React from 'react';
import './fileUpload.scss';
import { BsFillArrowUpCircleFill } from "react-icons/bs";
import './pageReload.scss';

const PageReload = ({ formpage, setFormpage,files, saveFileImage }) => {
  
  const handleSaveFileImage = (e) =>{
    console.log(e.target.files)
    setFormpage(false);
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
            <div className="drag-text">
                <BsFillArrowUpCircleFill size="30"></BsFillArrowUpCircleFill>
                <p>드래그하거나 클릭하여 업로드</p>
             </div>
      </>
  );
};

export default PageReload;