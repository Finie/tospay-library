package net.tospay.auth.viewmodelfactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import net.tospay.auth.remote.repository.CardRepository;
import net.tospay.auth.ui.account.card.LinkCardViewModel;
import net.tospay.auth.utils.SharedPrefManager;

public class LinkCardViewModelFactory  extends ViewModelProvider.NewInstanceFactory{
    private final CardRepository cardRepository;
    private final SharedPrefManager sharedPrefManager;

    public LinkCardViewModelFactory(CardRepository cardRepository,SharedPrefManager sharedPrefManager) {
        this.cardRepository = cardRepository;
        this.sharedPrefManager =sharedPrefManager;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LinkCardViewModel.class)) {
            //noinspection unchecked
            return (T) new LinkCardViewModel(cardRepository,sharedPrefManager);
        }

        throw new IllegalArgumentException("Unknown ViewModel Class: " + modelClass.getName());
    }
}
