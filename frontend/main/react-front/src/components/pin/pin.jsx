import React from 'react';
import Header from '../header/header';
import Search from '../search/search';
import './pin.scss';
import { ButtonGroup, Button } from 'react-bootstrap';
import { FiSend, FiExternalLink} from 'react-icons/fi';
import { AiOutlineBook } from 'react-icons/ai';
import { BsFillArrowUpCircleFill } from 'react-icons/bs';
const Pin = () => {
    return (
        <div>
           <Header></Header>
           <Search></Search>
           <div className="container">
                <div className='pin-container'>
                    <div className='img-conteinr'>
                        <img src="" alt="" />
                    </div>
                    <div className='data-container'>
                        {/*function container*/}
                        <div className='function-container'>
                            <div></div>
                        </div>
                         {/*user container*/}
                         <div className='user-id-container'>
                             <span></span>
                         </div>
                     {/*title container*/}
                     <div className='title-container'>
                             <div></div>
                         </div>
                          {/*description container*/}
                     <div className='description-container'>
                             <div>
                                 <p></p>
                             </div>
                             <div className='folwer-container'>
                                   {/*description container*/}
                                 <div></div>
                                   {/*button container*/}
                                 <div></div>
                             </div>
                             <div className=''></div>
                         </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Pin;