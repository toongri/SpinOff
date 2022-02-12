import './App.css';
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import Home from './components/home/home';
import PinBuild from './components/pin-build/pinBuild';
import Pin from './components/pin/pin';
import FileUpload from './components/pin-build/pinBuild.jsx';
import PinAll from './components/pinAll/pinAll.jsx'
import Collection from './components/collection/collection.jsx'
import PinDetail from './components/pinDetail/pinDetail';

function App({network}) {

  return (
    <Router>
      <Routes>
        <Route path = '/' element = {<Home network = {network} />}></Route>
        <Route path = '/pin-build' element = {<PinBuild />}></Route>
        <Route path = '/pin' element = {<Pin />}></Route>
          <Route path = '/fileUpload' element = {<FileUpload />}></Route>
         <Route path = '/pinAll' element = {<PinAll />}></Route>
         <Route path = '/collection' element = {<Collection />}></Route>
         <Route path = '/pinDetail' element = {<PinDetail />}></Route>
      </Routes>
    </Router>
  );
}

export default App;
