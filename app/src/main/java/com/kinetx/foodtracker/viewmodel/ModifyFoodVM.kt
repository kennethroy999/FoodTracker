package com.kinetx.foodtracker.viewmodel

import android.app.Application
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kinetx.foodtracker.database.DatabaseMain
import com.kinetx.foodtracker.database.DatabaseRepository
import com.kinetx.foodtracker.database.FoodDB
import com.kinetx.foodtracker.enums.ServingUnit
import com.kinetx.foodtracker.fragment.ModifyFoodFragmentArgs
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class ModifyFoodVM(application: Application, args: ModifyFoodFragmentArgs): AndroidViewModel(application) {


    private val _fragmentTitle = MutableLiveData<String>()
    val fragmentTitle : LiveData<String>
        get() = _fragmentTitle

    private val _isEditVisible = MutableLiveData<Int>()
    val isEditVisible : LiveData<Int>
        get() = _isEditVisible

    private val _isCreateVisible = MutableLiveData<Int>()
    val isCreateVisible : LiveData<Int>
        get() = _isCreateVisible








    var foodName = MutableLiveData<String>()
    var foodDesc = MutableLiveData<String>()
    var foodServing = MutableLiveData<String>()
    var foodCalories = MutableLiveData<String>()
    var foodCarbs = MutableLiveData<String>()
    var foodFiber = MutableLiveData<String>()
    var foodSugar = MutableLiveData<String>()
    var foodProtein = MutableLiveData<String>()
    var foodFat = MutableLiveData<String>()
    var foodFatSat = MutableLiveData<String>()
    var foodFatUnSat = MutableLiveData<String>()
    var foodCholesterol = MutableLiveData<String>()
    var foodSodium = MutableLiveData<String>()
    var foodPotassium = MutableLiveData<String>()
    var foodIron = MutableLiveData<String>()
    var foodVitaminD = MutableLiveData<String>()





    private val spinnerUnitList = listOf("g","ml")

    private val _foodUnitSpinnerSelected = MutableLiveData<Int>()
    val foodUnitSpinnerSelected : LiveData<Int>
        get() = _foodUnitSpinnerSelected

    private val _foodUnitSpinner = MutableLiveData<List<String>>()
    val foodUnitSpinner : LiveData<List<String>>
        get() = _foodUnitSpinner




    private val _foodDB = MutableLiveData<FoodDB>()
    val foodDB : LiveData<FoodDB>
        get() = _foodDB

    private val repository : DatabaseRepository

    init {

        _foodUnitSpinner.value = spinnerUnitList
        _foodUnitSpinnerSelected.value = 0

        val userDao = DatabaseMain.getInstance(application).databaseDao
        repository = DatabaseRepository(userDao)

        _foodDB.value = FoodDB()
        Log.i("III food id",args.foodId.toString())
        when(args.foodId)
        {
            -1L->
            {
                _isCreateVisible.value = View.VISIBLE
                _isEditVisible.value = View.GONE
                _fragmentTitle.value = "Create Food"
            }
            else->
            {
                _isCreateVisible.value = View.GONE
                _isEditVisible.value = View.VISIBLE
                _fragmentTitle.value = "Edit Food"
                Log.i("III food id 2",args.foodId.toString())
                viewModelScope.launch(Dispatchers.IO)
                {
                    val tt = repository.getFoodWithId(args.foodId)
                    _foodDB.postValue(tt)
                }
            }
        }

    }

    fun updateInterface() {

        foodName.value = _foodDB.value?.foodName
        foodDesc.value = _foodDB.value?.foodDesc

        foodServing.value = convertToString(_foodDB.value?.foodServingSize)
        _foodUnitSpinnerSelected.value = when(_foodDB.value?.foodServingUnit)
        {
            ServingUnit.G->0
            ServingUnit.ML->1
            else -> 0
        }

        foodCalories.value = convertToString(_foodDB.value?.foodCalories)
        foodCarbs.value = convertToString(_foodDB.value?.foodCarbs)
        foodFiber.value = convertToString(_foodDB.value?.foodFiber)
        foodSugar.value = convertToString(_foodDB.value?.foodSugar)
        foodProtein.value = convertToString(_foodDB.value?.foodProtein)
        foodFat.value = convertToString(_foodDB.value?.foodFat)
        foodFatSat.value = convertToString(_foodDB.value?.foodFatSat)
        foodFatUnSat.value = convertToString(_foodDB.value?.foodFatUnSat)
        foodCholesterol.value = convertToString(_foodDB.value?.foodCholesterol)
        foodSodium.value = convertToString(_foodDB.value?.foodSodium)
        foodPotassium.value = convertToString(_foodDB.value?.foodPotassium)
        foodIron.value = convertToString(_foodDB.value?.foodIron)
        foodVitaminD.value = convertToString(_foodDB.value?.foodVitaminD)
    }

    private fun convertToString(float: Float?) : String
    {
        return if (float==0.0f) {
            ""
        } else {
            float.toString()
        }
    }

    fun convertToFloat(s:String) : Float
    {
        return if (s=="" || s==".") {
            0.0f
        } else {
            s.toFloat()
        }
    }

    fun createFood(selectedUnitPosition: Int) : Boolean {

        _foodDB.value?.foodId = 0
        _foodDB.value?.foodServingUnit = when(selectedUnitPosition)
        {
            0 -> ServingUnit.G
            1 -> ServingUnit.ML
            else-> ServingUnit.G
        }

        if (checkFoodData(_foodDB.value))
        {
            GlobalScope.launch(Dispatchers.IO)
            {
                repository.insertFood(foodDB.value!!)
            }

            return true
        }
        return false

    }


    fun updateFood(selectedUnitPosition: Int): Boolean
    {
        _foodDB.value?.foodServingUnit = when(selectedUnitPosition)
        {
            0 -> ServingUnit.G
            1 -> ServingUnit.ML
            else-> ServingUnit.G
        }

        if (checkFoodData(_foodDB.value))
        {
            GlobalScope.launch(Dispatchers.IO)
            {
                repository.updateFood(foodDB.value!!)
            }

            return true
        }

        return false
    }

    fun deleteFood() {
        GlobalScope.launch(Dispatchers.IO)
        {
            repository.deleteFood(foodDB.value!!)
            repository.deleteFoodLogWithFood(foodDB.value!!.foodId)
        }
    }


    private fun checkFoodData(foodDB: FoodDB?) :Boolean
    {

        val context = getApplication<Application>().applicationContext

        if (foodDB?.foodName=="")
        {
            Toast.makeText(context,"Food name cannot be empty",Toast.LENGTH_SHORT).show()
            return false
        }
        if (foodDB?.foodDesc=="")
        {
            Toast.makeText(context,"Food description cannot be empty",Toast.LENGTH_SHORT).show()
            return false
        }
        if (foodDB?.foodServingSize==0.0f)
        {
            Toast.makeText(context,"Serving size value cannot be empty",Toast.LENGTH_SHORT).show()
            return false
        }
        if (foodDB?.foodCalories==0.0f)
        {
            Toast.makeText(context,"Calories value cannot be empty",Toast.LENGTH_SHORT).show()
            return false
        }
        if (foodDB?.foodCarbs==0.0f)
        {
            Toast.makeText(context,"Carbs value cannot be empty",Toast.LENGTH_SHORT).show()
            return false
        }
        if (foodDB?.foodProtein==0.0f)
        {
            Toast.makeText(context,"Protein value cannot be empty",Toast.LENGTH_SHORT).show()
            return false
        }
        if (foodDB?.foodFat==0.0f)
        {
            Toast.makeText(context,"Fat value cannot be empty",Toast.LENGTH_SHORT).show()
            return false
        }


        return true
    }


}