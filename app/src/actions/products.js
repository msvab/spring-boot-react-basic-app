'use strict'
import {get, postJson, putJson} from '../utils/request'
import {RECEIVE_PRODUCTS, PRODUCT_CREATED, PRODUCT_UPDATED, PRICE_SET,
    TOGGLE_CREATE_PRODUCT, SHOW_EDIT_PRODUCT, SHOW_ADD_PRICE, SHOW_ERRORS} from '../constants/action-types'

export const receiveProducts = products => ({ type: RECEIVE_PRODUCTS, products })
export const productCreated = product => ({ type: PRODUCT_CREATED, product })
export const productUpdated = (id, product) => ({ type: PRODUCT_UPDATED, id, product })
export const priceSet = (id, price) => ({ type: PRICE_SET, id, price })

export const toggleCreateProduct = () => ({type: TOGGLE_CREATE_PRODUCT})
export const showEditProduct = id => ({type: SHOW_EDIT_PRODUCT, id})
export const showAddPrice = id => ({type: SHOW_ADD_PRICE, id})
export const showErrors = errors => ({type: SHOW_ERRORS, errors})

export function fetchProducts() {
  return async function(dispatch) {
    const response = await get('/products')

    if (response.ok) {
      const users = await response.json()
      dispatch(receiveProducts(users))
    }
  };
}

export function createProduct(product) {
  return async function (dispatch) {
    const response = await postJson('/products', product)

    if (response.ok) {
      const responseBody = await response.json()
      dispatch(productCreated(responseBody))
    } else {
      handleErrors(response, dispatch)
    }
  }
}

export function updateProduct(id, product) {
  return async function(dispatch) {
    const response = await putJson(`/products/${id}`, product)

    if (response.ok) {
      dispatch(productUpdated(id, product))
    } else {
      handleErrors(response, dispatch)
    }
  };
}

export function setPrice(id, price) {
  return async function(dispatch) {
    const response = await putJson(`/products/${id}/prices/${price.currency}`, {amount: price.amount})

    if (response.ok) {
      dispatch(priceSet(id, price))
    } else {
      handleErrors(response, dispatch)
    }
  };
}

async function handleErrors(response, dispatch) {
  const responseBody = await response.json()
  const errorMessages = responseBody.errors || (responseBody.message ? [responseBody.message] : ['Something wrong has happened'])
  dispatch(showErrors(errorMessages))
}
