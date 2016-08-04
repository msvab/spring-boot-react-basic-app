'use strict'
import React from 'react'
import {connect} from 'react-redux'

import {fetchProducts, toggleCreateProduct, showEditProduct, showAddPrice, setPrice} from '../actions/products'
import ProductForm from '../components/ProductForm'
import PricePointInput from '../components/PricePointInput'

class ProductsPage extends React.Component {

  toggleForm() {
    this.props.dispatch(toggleCreateProduct())
  }

  editProduct(id) {
    this.props.dispatch(showEditProduct(id))
  }

  addPrice(id) {
    this.props.dispatch(showAddPrice(id))
  }

  componentDidMount() {
    this.props.dispatch(fetchProducts());
  }

  render() {
    return (
        <div className="container">
          {this.props.products.create
              ? <ProductForm dispatch={this.props.dispatch} errors={this.props.products.errors}/>
              : <button onClick={::this.toggleForm} className="btn btn-default pull-right btn-sm">Add Product</button>}
          <br/>
          <table className="table table-condensed">
            <thead>
            <tr>
              <th width="150">Name</th>
              <th width="230">Description</th>
              <th width="180">Tags</th>
              <th width="100">Price Points</th>
              <th width="140">Actions</th>
            </tr>
            </thead>
            <tbody>
            {this.props.products.list.map(product => {
              return (
                  <tr key={product.id}>
                    {this.props.products.edit === product.id
                        ? <td colSpan="5"><ProductForm product={product}
                                                       dispatch={this.props.dispatch}
                                                       errors={this.props.products.errors}/></td>
                        : [
                      <td key="name">{product.name}</td>,
                      <td key="desc">{product.description}</td>,
                      <td key="tags">{product.tags.join(', ')}</td>,
                      <td key="prices">
                        <ul>
                          {product.prices.map(price => <li key={`${product.id}_${price.currency}`}>{price.amount} {price.currency}</li>)}
                        </ul>
                        {this.props.products.setPrice === product.id &&
                            <PricePointInput setPrice={(price) => this.props.dispatch(setPrice(product.id, price))}
                                             showSaveButton={true}
                                             errors={this.props.products.errors}/>
                        }
                      </td>,
                      <td key="action">
                        <button onClick={this.editProduct.bind(this, product.id)} className="btn btn-default btn-sm">Edit</button>
                        <button onClick={this.addPrice.bind(this, product.id)} className="btn btn-default btn-sm">Add Price</button>
                      </td>
                    ]
                    }
                  </tr>
              )
            })}
            </tbody>
          </table>
        </div>
    )
  }
}

function mapStateToProps(state) {
  return {products: state.products};
}

export default connect(mapStateToProps)(ProductsPage)