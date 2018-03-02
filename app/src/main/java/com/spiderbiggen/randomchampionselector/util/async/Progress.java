package com.spiderbiggen.randomchampionselector.util.async;

/**
 * Created on 1-3-2018.
 *
 * @author Stefan Breetveld
 */
public final class Progress {
    public static final int ERROR = -1;
    public static final int CONNECT_SUCCESS = 0;
    public static final int GET_INPUT_STREAM_SUCCESS = 1;
    public static final int PROCESS_INPUT_STREAM_IN_PROGRESS = 2;
    public static final int PROCESS_INPUT_STREAM_SUCCESS = 3;
    public static final int DOWNLOAD_SUCCESS = 4;

    private final boolean indeterminate;
    private final int progress;
    private final int max;
    private final int id;

    private Progress(int id, int progress, int max) {
        this(id, false, progress, max);
    }

    private Progress(int id) {
        this(id, true, -1, -1);
    }

    private Progress(int id, boolean indeterminate, int progress, int max) {
        this.id = id;
        this.indeterminate = indeterminate;
        this.progress = progress;
        this.max = max;
    }


}
