package com.webengage.demo.shopping.view.cart

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.webengage.demo.shopping.Constants.homeTAG
import com.webengage.demo.shopping.FragmentListener
import com.webengage.demo.shopping.view.home.Product
import com.webengage.demo.shopping.R


/**
 * A simple [Fragment] subclass.
 * Use the [CartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CartFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewModel: CartViewModel
    private lateinit var buttonLayout: LinearLayout
    private lateinit var cartItemsTitle: TextView
    private lateinit var buyNowButton: Button
    private lateinit var cancelButton: Button

    private var createTime: Long = 0L
    private var totalItems: Int = 0
    private var fragmentListener: FragmentListener? = null

    private val buttonOnClickListener = fun(product: Product) {
        viewModel.removeItemFromList(product)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createTime = System.currentTimeMillis()
    }

    override fun onStart() {
        super.onStart()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentListener = if (context is FragmentListener) {
            context
        } else {
            throw RuntimeException("$context must implement FragmentListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[CartViewModel::class.java]
        val cartItemsHolderRecyclerView =
            view.findViewById<RecyclerView>(R.id.cartItemsHolderRecycler)
        val layoutManager =
            LinearLayoutManager(this.context) // Replace 'this' with your activity or fragment context
        cartItemsHolderRecyclerView.layoutManager = layoutManager
        buttonLayout = view.findViewById(R.id.buttonLayout)
        cartItemsTitle = view.findViewById(R.id.cartItemsTitle)
        buyNowButton = view.findViewById(R.id.buyNowButton)
        cancelButton = view.findViewById(R.id.cancelButton)
        buyNowButton.setOnClickListener { buyNowClicked() }
        cancelButton.setOnClickListener { cancelClicked() }
        val context = activity?.applicationContext
        val recyclerAdapter = CartRecyclerAdapter(
            emptyList(), buttonOnClickListener,
            context!!
        )
        cartItemsHolderRecyclerView.adapter = recyclerAdapter
        viewModel.getItemList().observe(viewLifecycleOwner, Observer { items ->
            totalItems = items.size
            recyclerAdapter.refreshList(items)
            updateOtherUI()
        })
        updateOtherUI()
    }

    private fun cancelClicked() {
        fragmentListener?.onFragmentAction(homeTAG)
    }

    private fun buyNowClicked() {
        viewModel.removeAllItems()
        fragmentListener?.onFragmentAction(homeTAG)
    }

    private fun updateOtherUI() {
        if (totalItems > 0) {
            buttonLayout.visibility = View.VISIBLE
            cartItemsTitle.text = "Cart Items"
        } else {
            buttonLayout.visibility = View.INVISIBLE
            cartItemsTitle.text = "Your cart is empty..!!"
        }

    }

}