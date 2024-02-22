package com.webengage.demo.shopping

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
import com.webengage.sdk.android.WebEngage

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
val weAnalytics = WebEngage.get().analytics()

/**
 * A simple [Fragment] subclass.
 * Use the [CartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CartFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var viewModel: CartViewModel
    lateinit var buttonLayout: LinearLayout
    lateinit var cartItemsTitle: TextView
    lateinit var buyNowButton: Button
    lateinit var cancelButton: Button

    private var createTime: Long = 0L
    private var totalItems: Int = 0
    private var fragmentListener: FragmentListener? = null

    private val buttonOnClickListener = fun(product: Product) {
        viewModel.removeItemFromList(product)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createTime = System.currentTimeMillis()
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

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
        viewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)
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
        fragmentListener?.onFragmentAction(HomeProductsFragment.TAG)
    }

    private fun buyNowClicked() {
        viewModel.removeAllItems()
        fragmentListener?.onFragmentAction(HomeProductsFragment.TAG)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CartFragment.
         */
        const val TAG = "CART"

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CartFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}