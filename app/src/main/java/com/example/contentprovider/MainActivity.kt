package com.example.contentprovider

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.contentprovider.databinding.ActivityMainBinding
import android.Manifest
import android.content.pm.PackageManager
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var contactList: MutableList<MyContact>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbarTB)
        binding.recyclerRV.layoutManager = LinearLayoutManager(this@MainActivity)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            permissionContact.launch(Manifest.permission.READ_CONTACTS)
        } else {
            getContact()
        }
    }

    @SuppressLint("Range")
    private fun getContact() {
        Toast.makeText(this@MainActivity,"Выводим контакты",Toast.LENGTH_SHORT).show()
        contactList = ArrayList()
        val phones = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "ASC"
        )
        while (phones!!.moveToNext()) {
            val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            contactList?.add(MyContact(name, phoneNumber))
        }
        phones.close()
        initAdapter(contactList as ArrayList<MyContact>)
    }

    private val permissionContact = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getContact()
        } else {
            Toast.makeText(this@MainActivity, "Отказано -> контакты", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initAdapter(contacts: MutableList<MyContact>) {
        val adapter = CustomAdapter(contacts)
        binding.recyclerRV.adapter = adapter
        binding.recyclerRV.setHasFixedSize(true)
        adapter.setOnItemClickListener(object :
            CustomAdapter.OnItemClickListener {
            override fun onItemClick(myContact: MyContact, position: Int) {
                val intent = Intent(this@MainActivity, ContactActivity::class.java)
                intent.putExtra("contact", myContact)
                startActivity(intent)
            }
        })
    }
}