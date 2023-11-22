package com.webengage.demo.shopping

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.json.JSONArray


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [HomeProductsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeProductsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var clickedProduct:Product
    private var clickedCategoryIndex:Int = -1


    val jsonArrayString: String = "[\n" +
            "    {\n" +
            "        \"title\": \"Electronics\",\n" +
            "        \"products\": [\n" +
            "            {\n" +
            "                \"image\": \"smartphone\",\n" +
            "                \"title\": \"Smartphone X\",\n" +
            "                \"price\": \"799.99₹\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"image\": \"laptop\",\n" +
            "                \"title\": \"Laptop Pro\",\n" +
            "                \"price\": \"1299.99₹\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"image\": \"headphone\",\n" +
            "                \"title\": \"Wireless Headphones\",\n" +
            "                \"price\": \"149.99₹\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"image\": \"tv\",\n" +
            "                \"title\": \"4K Smart TV\",\n" +
            "                \"price\": \"599.99₹\"\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"title\": \"Clothing\",\n" +
            "        \"products\": [\n" +
            "            {\n" +
            "                \"image\": \"denim\",\n" +
            "                \"title\": \"Men's Denim Jeans\",\n" +
            "                \"price\": \"49.99₹\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"image\": \"casual\",\n" +
            "                \"title\": \"Women's Casual Dress\",\n" +
            "                \"price\": \"39.99₹\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"image\": \"hoodie\",\n" +
            "                \"title\": \"Kids' Hoodie\",\n" +
            "                \"price\": \"29.99₹\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"image\": \"shoes\",\n" +
            "                \"title\": \"Sports Shoes\",\n" +
            "                \"price\": \"69.99₹\"\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"title\": \"Home and Garden\",\n" +
            "        \"products\": [\n" +
            "            {\n" +
            "                \"image\": \"sofaset\",\n" +
            "                \"title\": \"Sofa Set\",\n" +
            "                \"price\": \"799.99₹\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"image\": \"kitchen\",\n" +
            "                \"title\": \"Kitchen Appliances\",\n" +
            "                \"price\": \"299.99₹\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"image\": \"garden\",\n" +
            "                \"title\": \"Garden Tools\",\n" +
            "                \"price\": \"59.99₹\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"image\": \"bedding\",\n" +
            "                \"title\": \"Bedding Set\",\n" +
            "                \"price\": \"99.99₹\"\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "]"
    val categories = mutableListOf<ProductCategory>()
    val imageLayoutsResourceId = intArrayOf(
        R.id.imageContainer_1,
        R.id.imageContainer_2,
        R.id.imageContainer_3
    )

    private lateinit var productDetailActivityResultLauncher: ActivityResultLauncher<Intent>




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        productDetailActivityResultLauncher= registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("HomeProductsFragment","productDetailActivityResultLauncher called " )
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val returnedBoolean = data?.getBooleanExtra("added",false)
                Log.d("HomeProductsFragment","returnedBoolean added "+ returnedBoolean)
                if (returnedBoolean != false) {
                    updateCartData()
                }
            }
        }
        stringToJsonObjectModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        Log.d("HomeProductsFragment", "onCreateView "+categories.size)
        // Inflate the layout for this fragment
        return inflater.inflate(
            com.webengage.demo.shopping.R.layout.fragment_product_list,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("HomeProductsFragment", "onViewCreated "+categories.size)
        addContentToScrollView()

    }

    override fun onStart() {
        super.onStart()

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeProductsFragment.
         */
        const val TAG = "HOME"
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeProductsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun stringToJsonObjectModel() {
// Parse the JSON array
        val jsonArray = JSONArray(jsonArrayString)

// Create a list to store the ProductCategory objects
        if(categories.size>0){
            return
        }

        for (i in 0 until jsonArray.length()) {
            val categoryObject = jsonArray.getJSONObject(i)

            val title = categoryObject.getString("title")
            val productsArray = categoryObject.getJSONArray("products")

            val products = mutableListOf<Product>()

            // Iterate through the products array
            for (j in 0 until productsArray.length()) {
                val productObject = productsArray.getJSONObject(j)

                val image = productObject.getString("image")
                val productTitle = productObject.getString("title")
                val price = productObject.getString("price")

                val product = Product(image, productTitle, price)
                products.add(product)
                clickedProduct = product
            }

            val category = ProductCategory(title, products)
            categories.add(category)
        }
        /*val viewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)
        if(clickedProduct!=null)
            viewModel.updateItemList(clickedProduct)*/

    }

    val headingsResourceId = intArrayOf(
        R.id.heading_1,
        R.id.heading_2,
        R.id.heading_3
    )

    private fun addContentToScrollView() {
        val imageHolderLinearLayout = view?.findViewById<LinearLayout>(R.id.imageContainer_1)
        var index = 0;
        val imageCount = imageHolderLinearLayout?.childCount
        if (imageCount != null && imageCount > 0) {
            //return
        }
        val timeLocal = System.currentTimeMillis()
        Log.d("addContentToScrollView", "imageCount "+imageCount)
        Log.d("addContentToScrollView", "time start "+timeLocal)
        for (category in categories) {
            val textView = view?.findViewById<TextView>(headingsResourceId[index])
            textView?.setText(category.title)
            //Log.d("addContentToScrollView", "Product: Title ${category.title}")
            addIconstoScrollView(category.products, index)
            index += 1;
        }
        Log.d("addContentToScrollView", "time end "+(System.currentTimeMillis() - timeLocal))

    }

    private fun addIconstoScrollView(products: List<Product>, index: Int) {

        val imageHolderLinearLayout =
            view?.findViewById<LinearLayout>(imageLayoutsResourceId[index])
        val inflater = LayoutInflater.from(activity?.applicationContext)
        for (product in products) {
            val childLayout = inflater.inflate(R.layout.child_layout, null) as FrameLayout
            val imageView = childLayout.findViewById<ImageView>(R.id.productIconImageView)
            val resId = resources.getIdentifier(product.image, "drawable", activity?.packageName)
            imageView.setImageResource(resId)
            val textView = childLayout.findViewById<TextView>(R.id.productShortDescTextView)
            textView.text = product.title
            // Add the child layout to the horizontalLayout
            imageHolderLinearLayout?.addView(childLayout)
            childLayout.setOnClickListener {
                startDetailsActivityForResult(product,index)
            }
        }

    }

    // ...

    private fun updateCartData(){
        val viewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)
        if(clickedProduct!=null){
            Log.d("HomeProductsFragment","updateCartData "+clickedProduct.title)
            viewModel.updateItemList(clickedProduct)
        }
    }
    fun startDetailsActivityForResult(product: Product, index: Int) {
        clickedProduct = product
        clickedCategoryIndex = index
        val intent = Intent(context, ProductDetailActivity::class.java)
        intent.putExtra("product",product)
        productDetailActivityResultLauncher.launch(intent)
    }
}