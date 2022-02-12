import {SEARCH} from './actions';
const initialState= {items:[]};

export function doSpinOff(previousState = initialState, action){

    if(action.type === SEARCH){
        return {
            ...previousState.items, items: action.items 
        }
    }

    return previousState;
}