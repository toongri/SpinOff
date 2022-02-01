import './App.css';
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import Home from './components/home/home';
import PinBuild from './components/pin-build/pinBuild';
import Pin from './components/pin/pin';
import FileUpload from './components/pin-build/pinBuild.jsx';

function App({network}) {

  return (
    <Router>
      <Routes>
        <Route path = '/' element = {<Home network = {network} />}></Route>
        <Route path = '/pin-build' element = {<PinBuild />}></Route>
        <Route path = '/pin' element = {<Pin />}></Route>
          <Route path = '/fileUpload' element = {<FileUpload />}></Route>
      </Routes>
    </Router>
  );
}

export default App;
