package android.print;

/**
 * Test-only bridge for callbacks whose constructors are package-private in the Android SDK.
 */
public final class HtmlPreviewerPrintCallbackFactory {

    private HtmlPreviewerPrintCallbackFactory() {
    }

    public static PrintDocumentAdapter.LayoutResultCallback layout(
            final LayoutListener listener
    ) {
        return new PrintDocumentAdapter.LayoutResultCallback() {
            @Override
            public void onLayoutCancelled() {
                listener.onCancelled();
            }

            @Override
            public void onLayoutFailed(CharSequence message) {
                listener.onFailed(message);
            }

            @Override
            public void onLayoutFinished(PrintDocumentInfo info, boolean changed) {
                listener.onFinished(info);
            }
        };
    }

    public static PrintDocumentAdapter.WriteResultCallback write(
            final WriteListener listener
    ) {
        return new PrintDocumentAdapter.WriteResultCallback() {
            @Override
            public void onWriteCancelled() {
                listener.onCancelled();
            }

            @Override
            public void onWriteFailed(CharSequence message) {
                listener.onFailed(message);
            }

            @Override
            public void onWriteFinished(PageRange[] pages) {
                listener.onFinished();
            }
        };
    }

    public interface LayoutListener {
        void onCancelled();

        void onFailed(CharSequence message);

        void onFinished(PrintDocumentInfo info);
    }

    public interface WriteListener {
        void onCancelled();

        void onFailed(CharSequence message);

        void onFinished();
    }
}
