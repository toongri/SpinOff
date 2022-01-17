import React from 'react';
import './header.scss';
import NotificationsNoneIcon from '@mui/icons-material/NotificationsNone';
import IconButton from '@mui/material/IconButton';
import SendIcon from '@mui/icons-material/Send';
import PersonOutlineIcon from '@mui/icons-material/PersonOutline';
import MenuIcon from '@mui/icons-material/Menu';
import ButtonGroups from '@mui/material/ButtonGroup';
import { makeStyles } from '@mui/styles';
import Button from '@mui/material/Button';

const useStyles = makeStyles({
  btngroups: {
    backgroundColor: '#f5f6fa',
    paddingRight: '20px'
  },
  navBtn:{
    color: 'grey',
  }
});

const Header = () => {
  const classes = useStyles();

  return (
    <div className = "nav_container">
      <nav>     
        <header className ="nav-header">
            <div className ="nav-title">
                LOGO
             </div>
        </header>
        <div className='nav-links'>
          <ButtonGroups>
            <Button className={classes.navBtn} variant = "text">도슨트</Button>
            <Button className={classes.navBtn} variant = "text">소셜링</Button>
           </ButtonGroups>
           </div>
      </nav>
            <ButtonGroups  className={classes.btngroups}>
         <IconButton >
            <NotificationsNoneIcon/>
         </IconButton>
         <IconButton>
           <SendIcon/>
        </IconButton>
        
        <IconButton>
          <PersonOutlineIcon />
        </IconButton>

        <IconButton>
          <MenuIcon />
        </IconButton>
        </ButtonGroups>
    </div>
  )
}

export default Header;