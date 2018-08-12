package io.github.stack07142.rxjava_android_samples.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.stack07142.rxjava_android_samples.BaseApplication
import io.github.stack07142.rxjava_android_samples.R
import io.github.stack07142.rxjava_android_samples.RVDividerItemDecoration
import io.github.stack07142.rxjava_android_samples.RVLoggerAdapter
import io.github.stack07142.rxjava_android_samples.retrofit.GithubService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_pseudo_cache.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

class PseudoCacheFragment : BaseFragment() {
    private var loggerAdapter: RVLoggerAdapter? = null
    private var subLoggerAdapter: RVLoggerAdapter? = null
    private var contributionMap: MutableMap<String, Long> = mutableMapOf()
    private var githubService: GithubService? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pseudo_cache, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupLogger()
        githubService = (activity?.applicationContext as BaseApplication).githubService
    }

    private fun setupLogger() {
        loggerAdapter = RVLoggerAdapter(30)
        rv_logger.adapter = loggerAdapter
        rv_logger.addItemDecoration(RVDividerItemDecoration.Builder(activity, RVDividerItemDecoration.VERTICAL_SKIP_FIRST)
                .build())

        subLoggerAdapter = RVLoggerAdapter(10)
        rv_sub_logger.adapter = subLoggerAdapter
        rv_sub_logger.addItemDecoration(RVDividerItemDecoration.Builder(activity, RVDividerItemDecoration.VERTICAL_SKIP_FIRST)
                .build())

        btn_pseudoCache_concat.setOnClickListener {
            clear()
            tv_desc.text = getString(R.string.msg_pseudoCache_demoInfo_concat)

            Observable.concat(getSlowCachedDiskData(), getFreshNetworkData())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { contributor ->
                                contributionMap[contributor.login] = contributor.contributions
                                loggerAdapter?.clear()
                                logAll(mapAsList(contributionMap))
                            },
                            {
                                Timber.e(it, "onError")
                            },
                            {
                                Timber.d("onSuccess")
                            }
                    )
        }

        btn_pseudoCache_concatEager.setOnClickListener {
            clear()
            tv_desc.text = getString(R.string.msg_pseudoCache_demoInfo_concatEager)

            Observable.concatEager(mutableListOf(getSlowCachedDiskData(), getFreshNetworkData()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { contributor ->
                                contributionMap[contributor.login] = contributor.contributions
                                loggerAdapter?.clear()
                                logAll(mapAsList(contributionMap))
                            },
                            {
                                Timber.e(it, "onError")
                            },
                            {
                                Timber.d("onSuccess")
                            }
                    )
        }

        btn_pseudoCache_merge.setOnClickListener {
            clear()
            tv_desc.text = getString(R.string.msg_pseudoCache_demoInfo_merge)

            Observable.merge(getCachedDiskData(), getFreshNetworkData())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { contributor ->
                                contributionMap[contributor.login] = contributor.contributions
                                loggerAdapter?.clear()
                                logAll(mapAsList(contributionMap))
                            },
                            {
                                Timber.e(it, "onError")
                            },
                            {
                                Timber.d("onSuccess")
                            }
                    )
        }

        btn_pseudoCache_mergeSlowDisk.setOnClickListener {
            clear()
            tv_desc.text = getString(R.string.msg_pseudoCache_demoInfo_mergeSlowDisk)

            Observable.merge(getSlowCachedDiskData(), getFreshNetworkData())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { contributor ->
                                contributionMap[contributor.login] = contributor.contributions
                                loggerAdapter?.clear()
                                logAll(mapAsList(contributionMap))
                            },
                            {
                                Timber.e(it, "onError")
                            },
                            {
                                Timber.d("onSuccess")
                            }
                    )
        }

        btn_pseudoCache_mergeOptimized.setOnClickListener {
            clear()
            tv_desc.text = getString(R.string.msg_pseudoCache_demoInfo_mergeOptimized)

            getFreshNetworkData()
                    ?.publish { t: Observable<GithubService.Contributor> ->
                        Observable.merge(t, getCachedDiskData().takeUntil(t))
                    }
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(
                            { contributor ->
                                contributionMap[contributor.login] = contributor.contributions
                                loggerAdapter?.clear()
                                logAll(mapAsList(contributionMap))
                            },
                            {
                                Timber.e(it, "onError")
                            },
                            {
                                Timber.d("onSuccess")
                            }
                    )
        }

        btn_pseudoCache_mergeOptimizedSlowDisk.setOnClickListener {
            clear()
            tv_desc.text = getString(R.string.msg_pseudoCache_demoInfo_mergeOptimizedSlowDisk)

            getFreshNetworkData()
                    ?.publish { t: Observable<GithubService.Contributor> ->
                        Observable.merge(t, getSlowCachedDiskData().takeUntil(t))
                    }
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(
                            { contributor ->
                                contributionMap[contributor.login] = contributor.contributions
                                loggerAdapter?.clear()
                                logAll(mapAsList(contributionMap))
                            },
                            {
                                Timber.e(it, "onError")
                            },
                            {
                                Timber.d("onSuccess")
                            }
                    )
        }
    }

    private fun clear() {
        loggerAdapter?.clear()
        subLoggerAdapter?.clear()

        contributionMap = mutableMapOf()
    }

    private fun log(msg: String) {
        loggerAdapter?.add(msg)
    }

    private fun logAll(msgList: List<String>) {
        loggerAdapter?.addAll(msgList)
    }

    private fun log(msg: String, vararg formatArgs: Any) {
        loggerAdapter?.add(String.format(msg, *formatArgs))
    }

    private fun subLog(msg: String) {
        subLoggerAdapter?.add(msg)
    }

    private fun subLog(msg: String, vararg formatArgs: Any) {
        subLoggerAdapter?.add(String.format(msg, *formatArgs))
    }

    private fun getFreshNetworkData(): Observable<GithubService.Contributor>? {
        return githubService?.contributors("square", "retrofit")
                ?.flatMap { t -> Observable.fromIterable(t) }
                ?.doOnSubscribe {
                    AndroidSchedulers.mainThread().scheduleDirect {
                        subLog("(network) subscribed")
                    }
                }
                ?.doOnComplete {
                    AndroidSchedulers.mainThread().scheduleDirect {
                        subLog("(network) completed")
                    }
                }
    }

    private fun getCachedDiskData(): Observable<GithubService.Contributor> {
        val list = ArrayList<GithubService.Contributor>()
        val map = dummyDiskData()

        for (username in map.keys) {
            val c = GithubService.Contributor()
            c.login = username
            c.contributions = map[username] ?: 0L
            list.add(c)
        }

        return Observable.fromIterable(list)
                .doOnSubscribe { _ ->
                    AndroidSchedulers.mainThread().scheduleDirect {
                        subLog("(disk) cache subscribed")
                    }
                }
                .doOnComplete {
                    AndroidSchedulers.mainThread().scheduleDirect {
                        subLog("(disk) cache completed")
                    }
                }
    }

    private fun getSlowCachedDiskData(): Observable<GithubService.Contributor> {
        return Observable.timer(2, TimeUnit.SECONDS).flatMap<GithubService.Contributor> { _ -> getCachedDiskData() }
    }

    private fun dummyDiskData(): Map<String, Long> {
        val map = HashMap<String, Long>()
        map["JakeWharton"] = 0L
        map["pforhan"] = 0L
        map["edenman"] = 0L
        map["swankjesse"] = 0L
        map["bruceLee"] = 0L
        return map
    }

    private fun mapAsList(map: MutableMap<String, Long>): List<String> {
        val list = ArrayList<String>()

        for (username in map.keys) {
            val rowLog = String.format("%s [%d]", username, contributionMap[username])
            list.add(rowLog)
        }

        return list
    }
}