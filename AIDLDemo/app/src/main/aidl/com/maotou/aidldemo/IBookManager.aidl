// IBookManager.aidl
package com.maotou.aidldemo;

import com.maotou.aidldemo.Book;
import com.maotou.aidldemo.IOnNewBookListener;

interface IBookManager {

    List<Book> addBook(in Book book);

    List<Book> getBookList();

    void addNewBookListaner(IOnNewBookListener listener);

    void removeNewBookListener(IOnNewBookListener listener);
}
