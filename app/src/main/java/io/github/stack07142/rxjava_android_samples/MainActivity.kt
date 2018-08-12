package io.github.stack07142.rxjava_android_samples

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import io.github.stack07142.rxjava_android_samples.fragments.MainFragment
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private val backButtonBehaviorSubject = BehaviorSubject.createDefault(0L).toSerialized()
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(android.R.id.content, MainFragment(), this.toString())
                    .addToBackStack(MainFragment::class.java.simpleName)
                    .commit()
        }

        disposable = backButtonBehaviorSubject.toFlowable(BackpressureStrategy.BUFFER)
                .observeOn(AndroidSchedulers.mainThread())
                .buffer(2, 1)
                .map { it[0] to it[1] }
                .subscribe {
                    Timber.d("first= ${it.first}, second= ${it.second}")
                    if (it.second - it.first < 1500) {
                        finish()
                    } else {
                        Toast.makeText(this, "종료할까요?", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            backButtonBehaviorSubject.onNext(System.currentTimeMillis())
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        disposable?.dispose()
    }
}
