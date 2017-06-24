from sklearn.ensemble import AdaBoostClassifier
from sklearn.metrics import accuracy_score

from import_data import load_data

def main():
    x_train, y_train, x_test, y_test = load_data()

    clf = AdaBoostClassifier()
    print("Training classifier")
    clf.fit(x_train, y_train)

    print("Making test predictions")
    predictions = clf.predict(x_test)

    print("Accuracy: %s" % accuracy_score(y_test, predictions))


if __name__ == '__main__':
    main()
