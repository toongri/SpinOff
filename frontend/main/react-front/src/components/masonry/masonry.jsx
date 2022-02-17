import React, {useState} from 'react';
import './masonry.scss';
import ItemsDetail from "../itemsDetail/itemsDetail";
import { Button } from "react-bootstrap";
import { BsPencilFill } from "react-icons/bs";
import { useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import Masonry from "react-masonry-component";
import Modal from "../modal/modal";

const LayOut = ({lastItemElement}) => {  
    const [openModal, setOpenModal] = useState(false);

    let navigate = useNavigate();

    const items = useSelector((state) =>{
      console.log(state.items);
      return state.items;
    })

    const toggleModal = () =>{
      setOpenModal(!openModal)
    }

    return (
       <>
      <Masonry className='my-masonry'> 
          {items.map((item, index) => {
              return <ItemsDetail
                style={{
                   width: "278px",
                    }}
                    key={index}
                    item={item}
                    ref = {lastItemElement}
                  />
             }
             )
            }
        </Masonry>
        <div className="buttonGroup">
          <div className="buttonBox1">
            <button
              onClick={
                () =>{
                  setOpenModal(true)
                }
              }
              variant="secondary"
            >
              <BsPencilFill
                style={{
                  color: "#f9cf00",
                }}
                size = "40"
              ></BsPencilFill>
            </button>
            </div>
            <div className="buttonBox2">
            <button
              onClick={() => {
                
              }}
              variant="secondary"
            >
              HELP
            </button>
            </div>
        </div>
        {openModal ? <Modal></Modal> :  null}
        </>
    );
};

export default LayOut;