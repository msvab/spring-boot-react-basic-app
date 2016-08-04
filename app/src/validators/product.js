'use strict'

export default function validateProduct(product) {
  const errors = []

  product.prices.forEach(price => {
    if (price.amount === null || price.currency === '')
      errors.push('Price amount and currency are required')
  })

  if (product.prices.length === 0)
    errors.push('At least one price has to be defined')

  if (product.name === '' || product.description === '')
    errors.push('Product name and description are required')

  return errors;
}