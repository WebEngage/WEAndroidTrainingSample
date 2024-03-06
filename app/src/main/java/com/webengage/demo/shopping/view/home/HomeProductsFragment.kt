package com.webengage.demo.shopping.view.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.webengage.demo.shopping.view.cart.CartViewModel
import com.webengage.demo.shopping.view.productDetail.ProductDetailActivity
import com.webengage.demo.shopping.R
import kotlin.random.Random

/**
 * A simple [Fragment] subclass.
 * Use the [HomeProductsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeProductsFragment : Fragment() {

    private lateinit var clickedProduct: Product
    private var clickedCategoryIndex: Int = -1
    private lateinit var viewModel: HomeProductsViewModel
    private lateinit var parentView: View
    private lateinit var productDetailActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[HomeProductsViewModel::class.java]
    }

    private fun listenForCartUpdate() {
        productDetailActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val returnedBoolean = data?.getBooleanExtra("added", false)
                    if (returnedBoolean != false) {
                        updateCartData()
                    }
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        parentView = inflater.inflate(
            R.layout.fragment_product_list,
            container,
            false
        )

        return parentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getProductsData().observe(viewLifecycleOwner, Observer {
            initView(it)
        })
        clickedProduct = viewModel.fetchProducts()!!
        listenForCartUpdate()
    }

    private fun initView(productCategories: MutableList<ProductCategory>) {
        val containerLL = parentView.findViewById<LinearLayout>(R.id.container)

        for (category in productCategories) {
            val layoutInflater = LayoutInflater.from(activity)
            val categoryView = layoutInflater.inflate(
                R.layout.layout_product_list, null, false
            )
            containerLL.addView(categoryView)
            categoryView.id = Random.nextInt()
            val title = categoryView.findViewById<TextView>(R.id.tv_title)
            title.text = category.title
            createProductList(category, categoryView)
        }
    }

    private fun createProductList(category: ProductCategory, categoryView: View) {
        val recyclerView = categoryView.findViewById<RecyclerView>(R.id.rv_product_list)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        val adapter = ProductAdapter(activity, category.products, object : ProductClickListener {
            override fun onProductClick(pos: Int, product: Product) {
                startDetailsActivityForResult(product, pos)
            }

        })
        recyclerView.adapter = adapter
    }

    private fun updateCartData() {
        val viewModel = ViewModelProvider(requireActivity())[CartViewModel::class.java]
        viewModel.updateItemList(clickedProduct)
    }

    private fun startDetailsActivityForResult(product: Product, index: Int) {
        clickedProduct = product
        clickedCategoryIndex = index
        val intent = Intent(context, ProductDetailActivity::class.java)
        intent.putExtra("product", product)
        productDetailActivityResultLauncher.launch(intent)
    }
}