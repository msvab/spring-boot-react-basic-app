'use strict'
import React from 'react'
import {updateProduct, createProduct, showErrors} from '../actions/products'
import validateProduct from '../validators/product'
import ErrorDisplay from './ErrorDisplay'

export default class ProductForm extends React.Component {
  static propTypes = {
    dispatch: React.PropTypes.func.isRequired,
    errors: React.PropTypes.arrayOf(React.PropTypes.string)
  }

  static defaultProps = {
    product: {name: '', description: '', tags: [], prices: []}
  }

  async saveProduct(event) {
    event.preventDefault()

    const name = this.refs.name.value.trim()
    const description = this.refs.description.value.trim()
    const tags = this.refs.tags.value.split(',').map(tag => tag.trim()).filter(tag => tag !== '')

    const prices = Array.from(event.target.querySelectorAll('.js-edit-price-inputs'))
        .map(inputGroup => {
          const inputs = Array.from(inputGroup.querySelectorAll('input'))
          const amount = inputs[0].value.trim()
          return {amount: amount === '' ? null : +amount, currency: inputs[1].value.trim().toUpperCase()}
        })

    const newPriceAmount = this.refs.newPriceAmount.value.trim()
    const newPriceCurrency = this.refs.newPriceCurrency.value.trim()
    if (newPriceAmount !== '' || newPriceCurrency !== '')
      prices.push({amount: newPriceAmount === '' ? null : +newPriceAmount, currency: newPriceCurrency})

    const product = {name, description, tags, prices}
    const errors = validateProduct(product)
    if (errors.length > 0) {
      this.props.dispatch(showErrors(errors))
    } else {
      if (this.props.product.id)
        this.props.dispatch(updateProduct(this.props.product.id, product))
      else
        this.props.dispatch(createProduct(product))
    }
  }

  render() {
    return (
        <form onSubmit={::this.saveProduct} className="form">
          <legend>
            {this.props.product.id ? `Edit Product ${this.props.product.name}` : 'Create New Product' }
          </legend>
          <div className="form-group-sm">
            <label>Product Name</label>
            <input type="text" ref="name" name="name" className="form-control" placeholder="Name"
                   defaultValue={this.props.product.name}/>
          </div>
          <div className="form-group-sm">
            <label>Product Description</label>
            <textarea name="description" ref="description" className="form-control" rows="3"
                      defaultValue={this.props.product.description}/>
          </div>
          <div className="form-group-sm">
            <label>Product Tags (comma separated)</label>
            <textarea name="tags" ref="tags" className="form-control" rows="3" defaultValue={this.props.product.tags.join(', ')}/>
          </div>

          {this.props.product.prices.length > 0 &&
            <label>Product Price Points</label>
          }
          {this.props.product.prices.map(price => {
            return (
            <div key={price.currency} className="form-group-sm form-inline js-edit-price-inputs">
            <input type="number" step="0.01" className="form-control" placeholder="Amount" defaultValue={price.amount}/>
            <input type="text" className="form-control" placeholder="Currency" defaultValue={price.currency}/>
            </div>
            )
          })}

          <label>Add New Price Point</label>
          <div className="form-group-sm form-inline">
            <input type="number" ref="newPriceAmount" step="0.01" className="form-control" placeholder="Amount"/>
            <input type="text" ref="newPriceCurrency" className="form-control" placeholder="Currency"/>
          </div>

          <br/>
          <ErrorDisplay errors={this.props.errors}/>

          <button type="submit" className="btn btn-default btn-sm">Save</button>
        </form>
    )
  }
}
