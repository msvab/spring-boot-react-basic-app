'use strict'
import React from 'react'
import {render} from 'react-dom'
import {Provider} from 'react-redux'
import {createStore, applyMiddleware, compose} from 'redux';
import thunk from 'redux-thunk';

import ProductsPage from './pages/ProductsPage'
import reducer from './reducers/products'

const store = createStore(
    reducer,
    compose(
        applyMiddleware(thunk),
        window.devToolsExtension ? window.devToolsExtension() : f => f))

class App extends React.Component {

  render() {
    return (
        <div>
          <nav className="navbar navbar-default">
            <div className="container-fluid">
              <div className="navbar-header">
                <div className="navbar-brand">Basic Product Manager</div>
              </div>
            </div>
          </nav>

          <ProductsPage/>
        </div>
    )
  }
}

render((
    <Provider store={store}>
      <App/>
    </Provider>
), document.getElementById('app'))