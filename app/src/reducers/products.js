'use strict';
import {combineReducers} from 'redux'

import {RECEIVE_PRODUCTS, PRODUCT_CREATED, PRODUCT_UPDATED, PRICE_SET,
    TOGGLE_CREATE_PRODUCT, SHOW_EDIT_PRODUCT, SHOW_ADD_PRICE, SHOW_ERRORS} from '../constants/action-types'

const sortProducts = (a, b) => a.name > b.name

const INITIAL_STATE = {create: false, edit: null, setPrice: null, errors: [], list: []}

function products(state = INITIAL_STATE, action) {
  switch (action.type) {
    case RECEIVE_PRODUCTS: {
      return Object.assign({}, state, {list: action.products})
    }
    case PRODUCT_CREATED: {
      const newState = Object.assign({}, state, {create: false, errors: []})
      newState.list.push(action.product)
      newState.list.sort(sortProducts)
      return newState
    }
    case PRODUCT_UPDATED: {
      const newState = Object.assign({}, state, {edit: null, errors: []})
      const oldProduct = newState.list[newState.list.findIndex(product => product.id === action.id)]
      Object.assign(oldProduct, action.product)
      newState.list.sort(sortProducts)
      return newState
    }
    case PRICE_SET: {
      const newState = Object.assign({}, state, {setPrice: null, errors: []})
      const prices = newState.list[newState.list.findIndex(product => product.id === action.id)].prices
      const priceToUpdate = prices.find(price => price.currency === action.price.currency);
      if (priceToUpdate)
        Object.assign(priceToUpdate, action.price)
      else
        prices.push(action.price)
      return newState
    }
    case SHOW_ADD_PRICE:
      return Object.assign({}, state, {create: false, edit: null, setPrice: action.id, errors: []})
    case TOGGLE_CREATE_PRODUCT:
      return Object.assign({}, state, {create: !state.create, edit: null, setPrice: null, errors: []})
    case SHOW_EDIT_PRODUCT:
      return Object.assign({}, state, {edit: action.id, create: false, setPrice: null, errors: []})
    case SHOW_ERRORS:
      return Object.assign({}, state, {errors: action.errors})
    default:
      return state
  }
}

export default combineReducers({
  products
})