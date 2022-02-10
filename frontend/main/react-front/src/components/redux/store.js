import {createStore} from 'redux';
import {doSpinOff} from './reducer';

const store = createStore(doSpinOff);

export default store;