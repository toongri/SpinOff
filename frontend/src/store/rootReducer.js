import { combineReducers } from 'redux';
import listFilterReducer from './ListFilter/reducer';

const rootReducer = combineReducers({
  listFilterReducer,
});

export default rootReducer;
