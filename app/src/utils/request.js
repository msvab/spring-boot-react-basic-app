'use strict'

const host = 'http://localhost:8080'
// const host = ''

export function get(url, options = {}) {
  return fetch(`${host}${url}`, Object.assign({method: 'GET', mode: 'cors'}, options))
}

export function postJson(url, data, options = {}) {
  const defaultOpts = {
    method: 'POST',
    mode: 'cors',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  }
  return fetch(`${host}${url}`, Object.assign(defaultOpts, options))
}

export function putJson(url, data, options = {}) {
  const defaultOpts = {
    method: 'PUT',
    mode: 'cors',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  }
  return fetch(`${host}${url}`, Object.assign(defaultOpts, options))
}