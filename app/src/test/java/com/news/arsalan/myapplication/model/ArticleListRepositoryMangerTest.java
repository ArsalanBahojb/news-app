package com.news.arsalan.myapplication.model;

import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * Created by Arsalan Bahojb
 * on 2017-12-04.
 */
@RunWith(MockitoJUnitRunner.class)
public class ArticleListRepositoryMangerTest {

    @Mock
     ArticleListRepositoryManger listRepository;
    @Mock
    Context context;

    public ArticleListRepositoryMangerTest()  throws Exception {

    }

    @Before
    public void setUp() throws Exception {
        listRepository = spy(ArticleListRepositoryManger.class);
        Mockito.doNothing().when(listRepository).saveData(context);
    }


    @Test
    public void shouldRepositoryEmptyInFirstRun(){
        Assert.assertTrue(listRepository.isEmpty());
    }

    @Test
    public void shouldReturnCorrectList() throws Exception {
        List<Article> articleList = Arrays.asList(new Article());
        listRepository.updateArticleList(context,articleList);
        Assert.assertFalse(listRepository.isEmpty());
    }

    @Test
    public void shouldListHasOneElement() throws Exception {
        List<Article> articleList = Arrays.asList(new Article());
        listRepository.updateArticleList(context,articleList);
        Assert.assertEquals(1,listRepository.getArticleList().size());

    }

}