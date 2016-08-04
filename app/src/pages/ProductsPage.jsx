'use strict'
import React from 'react'
import {connect} from 'react-redux'

import {fetchProducts, toggleCreateProduct, showEditProduct, showAddPrice, setPrice} from '../actions/products'
import ProductForm from '../components/ProductForm'
import ProductRow from '../components/ProductRow'

class ProductsPage extends React.Component {

  componentDidMount() {
    this.props.dispatch(fetchProducts());
  }

  render() {
    return (
        <div className="container">
          {this.props.products.create
              ? <ProductForm dispatch={this.props.dispatch} errors={this.props.products.errors}/>
              : <button className="btn btn-default pull-right btn-sm"
                        onClick={() => this.props.dispatch(toggleCreateProduct())}>Add Product</button>}
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
              const showEditForm = this.props.products.edit === product.id
              return showEditForm
                  ? <tr key={product.id}>
                      <td colSpan="5">
                        <ProductForm product={product} dispatch={this.props.dispatch} errors={this.props.products.errors}/>
                      </td>
                    </tr>
                  : <ProductRow key={product.id}
                                product={product}
                                showSetPrice={this.props.products.setPrice === product.id}
                                errors={this.props.products.errors}
                                setPrice={(id, price) => this.props.dispatch(setPrice(id, price))}
                                editProduct={id => this.props.dispatch(showEditProduct(id))}
                                addPrice={id => this.props.dispatch(showAddPrice(id))}/>
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