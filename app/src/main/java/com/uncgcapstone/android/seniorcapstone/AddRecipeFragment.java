package com.uncgcapstone.android.seniorcapstone;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import github.nisrulz.recyclerviewhelper.RVHAdapter;
import github.nisrulz.recyclerviewhelper.RVHItemClickListener;
import github.nisrulz.recyclerviewhelper.RVHItemDividerDecoration;
import github.nisrulz.recyclerviewhelper.RVHItemTouchHelperCallback;
import github.nisrulz.recyclerviewhelper.RVHViewHolder;
import mabbas007.tagsedittext.TagsEditText;

import static android.graphics.Color.BLACK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.RESULT;



/**
 * A simple {@link Fragment} subclass.
 */
public class AddRecipeFragment extends Fragment {

    private ImageView addPicture;
    private TextView recipeNameText;
    private Button publishButton;

    final int PICK_PIC = 0;
    Uri photoUri = null;
    SharedPreferences mSharedPreferences;

    private final static String TAG = "AddRecipeFragment";

    RecyclerView mRecyclerView;
    MyAdapter mAdapter;
    MyAdapter1 mAdapter1;
    List<String> mIngredients = new ArrayList<>();
    List<String> mIngredients2 = new ArrayList<>();
    List<String> mIngredients3 = new ArrayList<>();

    private EditText ingredientsText, ingredientsText3;
    private AutoCompleteTextView ingredientsText2;

    RecyclerView mRecyclerView1;
    private TextView stepsTitleText;
    private EditText stepsText;
    List<String> mSteps = new ArrayList<>();
    RecyclerView.LayoutManager mLinearLayoutManager, mLinearLayoutManager1;
    TextView ingredientsTitleText, placeholder_ingredients, placeholder_steps, tagsTitleText, placeholder_tags;
    EditText servesText, prepText, cookText;
    TagsEditText mTagsEditText;
    //CheckBox checkbox;
    View v;
    final String[] measurements = new String[] {
            "Tbsp", "Cup", "Oz", "Pt", "Gal", "Gals",  "Tsp", "Quart", "Quarts",
    };



    // url to create new product
    private String url_create_product = "http://3661590e.ngrok.io/android_connect/create_recipe.php";

    // JSON Node names
    private final String TAG_SUCCESS = "success";


    public AddRecipeFragment() {
        // Required empty public constructor
    }

    public static AddRecipeFragment newInstance() {
        AddRecipeFragment fragment = new AddRecipeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_recipe_add, container, false);

        ((MainActivity) getActivity()).setToolbar("Add a Recipe");

        //changes

        mTagsEditText = (TagsEditText) v.findViewById(R.id.tagsEditText);
        mTagsEditText.setTagsBackground(R.drawable.rounded_edittext_orange);
        mTagsEditText.setTextColor(BLACK);
        mTagsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, mTagsEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ingredientsTitleText = (TextView) v.findViewById(R.id.ingredientsTitleText);
        stepsTitleText = (TextView) v.findViewById(R.id.stepsTitleText);
        tagsTitleText = (TextView) v.findViewById(R.id.tagsTitleText);

        placeholder_ingredients = (TextView) v.findViewById(R.id.placeholder_ingredients);
        placeholder_ingredients.setVisibility(VISIBLE);
        placeholder_steps = (TextView) v.findViewById(R.id.placeholder_steps);
        placeholder_steps.setVisibility(VISIBLE);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.ingredientsRecyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mIngredients = new ArrayList<>();
        mIngredients2 = new ArrayList<>();
        mIngredients3 = new ArrayList<>();

        servesText = (EditText) v.findViewById(R.id.servesText);
        prepText = (EditText) v.findViewById(R.id.prepText);
        cookText = (EditText) v.findViewById(R.id.cookText);


        // Setup your adapter
        mAdapter = new MyAdapter(mIngredients, mIngredients2, mIngredients3);
        // Setup
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);


        // Setup onItemTouchHandler to enable drag and drop , swipe left or right
        ItemTouchHelper.Callback callback = new RVHItemTouchHelperCallback(mAdapter, true, true,
                true);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecyclerView);

        // Set the divider in the recyclerview
        mRecyclerView.addItemDecoration(new RVHItemDividerDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        // Set On Click Listener
        mRecyclerView.addOnItemTouchListener(new RVHItemClickListener(getActivity(), new RVHItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String value = "Clicked Item " + mIngredients.get(position) + " at " + position;
                Log.d("TAG", value);

            }
        }));

        mRecyclerView1 = (RecyclerView) v.findViewById(R.id.stepsRecyclerView);
        mRecyclerView1.setNestedScrollingEnabled(false);
        mSteps = new ArrayList<>();

        mAdapter1 = new MyAdapter1(mSteps);
        mRecyclerView1.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView1.setAdapter(mAdapter1);

        // Setup onItemTouchHandler to enable drag and drop , swipe left or right
        ItemTouchHelper.Callback callback1 = new RVHItemTouchHelperCallback(mAdapter1, true, true,
                true);
        ItemTouchHelper helper1 = new ItemTouchHelper(callback1);
        helper1.attachToRecyclerView(mRecyclerView1);

        // Set the divider in the recyclerview
        mRecyclerView1.addItemDecoration(new RVHItemDividerDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        // Set On Click Listener
        mRecyclerView1.addOnItemTouchListener(new RVHItemClickListener(getActivity(), new RVHItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String value = "Clicked Item " + mSteps.get(position) + " at " + position;
            }
        }));


        addPicture = (ImageView) v.findViewById(R.id.addPicture);
        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                startActivityForResult(chooserIntent, PICK_PIC);
            }
        });
        recipeNameText = (TextView) v.findViewById(R.id.testText);
        recipeNameText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                recipeNameText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        publish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };


        publishButton = (Button) v.findViewById(R.id.testButton);
        publishButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to publish this recipe? It will be publicly available for anyone to see.").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, measurements);
        ingredientsText2 = (AutoCompleteTextView)
                v.findViewById(R.id.ingredientsText2);
        ingredientsText2.setAdapter(adapter);
        ingredientsText2.setThreshold(1);


        ingredientsText = (EditText) v.findViewById(R.id.ingredientsText);
        ingredientsText3 = (EditText) v.findViewById(R.id.ingredientsText3);
        ingredientsText3.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (ingredientsText3.getText().toString().length() > 0) {
                        if(!(ingredientsText.getText().toString().equals("")) && !(ingredientsText2.getText().toString().equals(""))) {
                            placeholder_ingredients.setVisibility(GONE);
                            mIngredients.add(ingredientsText.getText().toString());
                            mIngredients2.add(ingredientsText2.getText().toString());
                            mIngredients3.add(ingredientsText3.getText().toString());
                            ingredientsText.setText("");
                            ingredientsText2.setText("");
                            ingredientsText3.setText("");
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.setBackgroundResource(R.drawable.rounded_edittext_white);
                        }
                        else if(!(ingredientsText.getText().toString().equals("")) && (ingredientsText2.getText().toString().equals("")) ){
                            placeholder_ingredients.setVisibility(GONE);
                            mIngredients.add(ingredientsText.getText().toString());
                            mIngredients2.add(" ");
                            mIngredients3.add(ingredientsText3.getText().toString());
                            ingredientsText.setText("");
                            ingredientsText2.setText("");
                            ingredientsText3.setText("");
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.setBackgroundResource(R.drawable.rounded_edittext_white);
                        }
                        else if((ingredientsText.getText().toString().equals("")) && !(ingredientsText2.getText().toString().equals(""))){
                            placeholder_ingredients.setVisibility(GONE);
                            mIngredients.add(" ");
                            mIngredients2.add(ingredientsText2.getText().toString());
                            mIngredients3.add(ingredientsText3.getText().toString());
                            ingredientsText.setText("");
                            ingredientsText2.setText("");
                            ingredientsText3.setText("");
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.setBackgroundResource(R.drawable.rounded_edittext_white);
                        }
                        else{
                            placeholder_ingredients.setVisibility(GONE);
                            mIngredients.add(" ");
                            mIngredients2.add(" ");
                            mIngredients3.add(ingredientsText3.getText().toString());
                            ingredientsText.setText("");
                            ingredientsText2.setText("");
                            ingredientsText3.setText("");
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.setBackgroundResource(R.drawable.rounded_edittext_white);
                        }
                    }
                    else{
                        SuperActivityToast.create(getActivity(), new Style(), Style.TYPE_BUTTON)
                                .setText("Please add an ingredient!")
                                .setDuration(Style.DURATION_SHORT)
                                .setFrame(Style.FRAME_LOLLIPOP)
                                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_PURPLE))
                                .setAnimations(Style.ANIMATIONS_POP).show();
                    }
                }
                return false;
            }
        });

        stepsText = (EditText) v.findViewById(R.id.stepsText);
        stepsText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (stepsText.getText().toString().length() > 0) {
                        placeholder_steps.setVisibility(GONE);
                        mSteps.add(stepsText.getText().toString());
                        stepsText.setText("");
                        mAdapter1.notifyDataSetChanged();
                        mRecyclerView1.setBackgroundResource(R.drawable.rounded_edittext_white);
                    }
                }
                return false;
            }
        });


        return v;
    }

    public void publish() {
        String recipeName = recipeNameText.getText().toString();
        String servestext = String.valueOf(servesText.getText().toString());
        String preptext = String.valueOf(prepText.getText().toString());
        String cooktext = String.valueOf(cookText.getText().toString());
        Calendar calendar = Calendar.getInstance();
        String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
        String uid = (String) ((MainActivity) getActivity()).getUid();
        mSharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        String tagsTemp = mTagsEditText.getText().toString();
        String[] tags = tagsTemp.split(" ");
        String[] ingredTags = new String[mIngredients3.size()];
        for(int i = 0; i < mIngredients3.size(); i++){
            ingredTags[i] = mIngredients3.get(i);
        }

        //String ingredTagsString = TextUtils.join(" ", mIngredients3);
        //String[] ingredTags = ingredTagsString.split(" ");

        String[] ingredients = new String[mIngredients.size()];
        String[] ingredients2 = new String[mIngredients.size()];
        String[] ingredients3 = new String[mIngredients.size()];

        for(int i = 0; i < mIngredients.size(); i++){
            ingredients[i] = mIngredients.get(i);
        }
        for(int i = 0; i < mIngredients2.size(); i++){
            ingredients2[i] = mIngredients2.get(i);
        }
        for(int i = 0; i < mIngredients3.size(); i++){
            ingredients3[i] = mIngredients3.get(i);
        }

        String[] steps = new String[mSteps.size()];
        for(int i = 0; i < steps.length; i++){
            steps[i] = mSteps.get(i);
        }

        String[] servesText = {servestext};
        String[] prepText = {preptext};
        String[] cookText = {cooktext};
        String[] dateTime = {datetime};
        String[] photouri = {photoUri.toString()};
        String[] UID = {uid};
        String user = mSharedPreferences.getString("email", "");
        String[] userName = {user};
        String[] recipename = {recipeName};


        String username = mSharedPreferences.getString("email", "");
        ((MainActivity) getActivity()).post(servesText, prepText, cookText, dateTime, photouri, UID, userName, recipename, tags, ingredients, ingredients2, ingredients3, steps, ingredTags); //SEPARATE THREAD???
    }

    @Override
    public void onResume() {
        super.onResume();
        mTagsEditText.setTagsBackground(R.drawable.rounded_edittext_orange);
        mTagsEditText.setTextColor(BLACK);
    }




    @Override
    public void onDestroyView(){
        super.onDestroyView();
        addPicture = null;
        publishButton = null;
        mRecyclerView1 = null;
        mRecyclerView = null;
        mAdapter1 = null;
        mAdapter = null;
        mIngredients = null;
        mSteps = null;
        mTagsEditText = null;
        photoUri = null;
        v = null;
        /**
         * The below code is used to allow memory to be GC'd correctly upon leaving the fragment
         * It may or may not be actually necessary
         */
        new Thread(new Runnable() {
            public void run() {
                Glide.get(getContext()).clearDiskCache();
            }
        }).start();
        Glide.get(getContext()).clearMemory();
        /**
         *
         */
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PIC) {
            if (resultCode == getActivity().RESULT_OK) {
                photoUri = data.getData();
                Glide.with(AddRecipeFragment.this).load(photoUri).centerCrop().diskCacheStrategy(RESULT).into(addPicture);
            } else {
            }
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<ItemViewHolder> implements RVHAdapter {
        private List<String> localIngredients;
        private List<String> localIngredients2;
        private List<String> localIngredients3;


        public MyAdapter(List<String> s, List<String> t, List<String> u) {
            localIngredients = s;
            localIngredients2 = t;
            localIngredients3 = u;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.ingredients_recyclerview, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder RVHViewHolder, int position) {
            String s,t,u = "";
            if(!(mIngredients.get(position).equals(""))) {
                s = mIngredients.get(position);
            }
            else{
                s = "";
            }
            if(!(mIngredients2.get(position).equals(""))) {
                t = mIngredients2.get(position);
            }
            else{
                t = "";
            }
            if(!(mIngredients3.get(position).equals(""))) {
                u = mIngredients3.get(position);
            }
            else{
                u = "";
            }
            RVHViewHolder.bindCard(s, t, u);
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            swap(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onItemDismiss(int position, int direction) {
            remove(position);
        }

        @Override
        public int getItemCount() {
            return mIngredients3.size();
        }

        private void remove(int position) {
            mIngredients.remove(position);
            mIngredients2.remove(position);
            mIngredients3.remove(position);
            notifyItemRemoved(position);
            if (mAdapter.getItemCount() == 0) {
                placeholder_ingredients.setVisibility(VISIBLE);
                mRecyclerView.setBackgroundResource(0);
            }
            for(int i = 0; i < mIngredients3.size(); i++){
                Log.d("remove", mIngredients.get(i) + mIngredients2.get(i) + mIngredients3.get(i));
            }


        }

        private void swap(int firstPosition, int secondPosition) {
            Collections.swap(mIngredients, firstPosition, secondPosition);
            notifyItemMoved(firstPosition, secondPosition);

            Collections.swap(mIngredients2, firstPosition, secondPosition);
            notifyItemMoved(firstPosition, secondPosition);

            Collections.swap(mIngredients3, firstPosition, secondPosition);
            notifyItemMoved(firstPosition, secondPosition);

            for(int i = 0; i < mIngredients3.size(); i++){
                Log.d("Swap", mIngredients.get(i) + mIngredients2.get(i) + mIngredients3.get(i));
            }
        }
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder implements RVHViewHolder {
        TextView recyclerText;


        public ItemViewHolder(View itemView) {
            super(itemView);
            recyclerText = (TextView) itemView.findViewById(R.id.recyclertext);
        }

        @Override
        public void onItemSelected(int actionstate) {
            System.out.println("Item is selected");
        }

        @Override
        public void onItemClear() {
            System.out.println("Item is unselected");


        }

        public void bindCard(String s, String t, String u) {
            recyclerText.setText(s + " " + t + " " + u);
        }
    }
//Begin Steps ViewHolder #####################
    public class MyAdapter1 extends RecyclerView.Adapter<ItemViewHolder1> implements RVHAdapter {
        private List<String> localSteps;

        public MyAdapter1(List<String> s) {
            localSteps = s;
        }

        @Override
        public ItemViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.steps_recyclerview, parent, false);
            return new ItemViewHolder1(view);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder1 RVHViewHolder, int position) {
            String s = mSteps.get(position);
            RVHViewHolder.bindCard(s);
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            swap(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onItemDismiss(int position, int direction) {
            remove(position);
        }

        @Override
        public int getItemCount() {
            return mSteps.size();
        }

        private void remove(int position) {
            mSteps.remove(position);
            notifyItemRemoved(position);
            if (mAdapter1.getItemCount() == 0) {
                mRecyclerView1.setBackgroundResource(0);
                placeholder_steps.setVisibility(VISIBLE);
            }
            for (String model : mSteps) {
                Log.d(TAG, model);
            }
        }

        private void swap(int firstPosition, int secondPosition) {
            Collections.swap(mSteps, firstPosition, secondPosition);
            notifyItemMoved(firstPosition, secondPosition);
        }
    }


    public class ItemViewHolder1 extends RecyclerView.ViewHolder implements RVHViewHolder {
        TextView recyclerText1;


        public ItemViewHolder1(View itemView) {
            super(itemView);
            recyclerText1 = (TextView) itemView.findViewById(R.id.recyclertext1);
        }

        @Override
        public void onItemSelected(int actionstate) {
            System.out.println("Item is selected");
        }


        @Override
        public void onItemClear() {
            System.out.println("Item is unselected");

        }

        public void bindCard(String s) {
            recyclerText1.setText(s);

        }
    }

}







