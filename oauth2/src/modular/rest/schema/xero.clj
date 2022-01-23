(ns modular.rest.schema.xero
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [martian.core :as martian]
   [schema.core :as s]))

(def contact
  {:ContactID s/Str
   :Phones [s/Any]
   :Addresses [s/Any]
   :ContactPersons [s/Any]
   :ContactGroups [s/Any]
   :HasValidationErrors s/Bool
   :Name s/Str})

(def line-item
  {(s/optional-key :Item) {:ItemID s/Str,
                           :Code s/Str,
                           :Name s/Str}
   :LineItemID s/Str
   (s/optional-key :LineAmount)  s/Num
   (s/optional-key :DiscountRate) s/Num
   (s/optional-key :UnitAmount) s/Num
   (s/optional-key :TaxType) s/Str
   (s/optional-key :Description) s/Str
   :Tracking [s/Any]
   (s/optional-key :AccountCode) s/Str
   (s/optional-key :Quantity) s/Num
   (s/optional-key :TaxAmount) s/Num
   (s/optional-key :ItemCode) s/Str})

(def invoice
  {:LineAmountTypes s/Str
   :AmountDue s/Num
   (s/optional-key :ExpectedPaymentDateString) s/Str
   :CreditNotes [s/Any]
   (s/optional-key :SentToContact) s/Bool
   :LineItems [line-item]
   :UpdatedDateUTC s/Str
   :HasAttachments s/Bool
   :Overpayments [s/Any]
   :CurrencyCode s/Str
   (s/optional-key :PlannedPaymentDateString) s/Str
   :InvoiceNumber s/Str
   (s/optional-key :PlannedPaymentDate) s/Str
   (s/optional-key :BrandingThemeID) s/Str
   (s/optional-key :FullyPaidOnDate) s/Str
   (s/optional-key :AmountPaid) s/Num
   :AmountCredited s/Num
   (s/optional-key :DueDate) s/Str
   :Prepayments [s/Any]
   :DateString s/Str
   (s/optional-key :Reference) s/Str
   :IsDiscounted s/Bool
   :HasErrors s/Bool
   :Total s/Num
   :Date s/Str
   :InvoiceID s/Str
   :TotalTax s/Num
   (s/optional-key :RepeatingInvoiceID) s/Str
   :Payments [s/Any]
   :SubTotal s/Num
   :Type s/Str
   (s/optional-key :ExpectedPaymentDate) s/Str
   :CurrencyRate s/Num
   (s/optional-key :DueDateString) s/Str
   :Contact contact
   :Status s/Str})

(def contact-full
  {:IsSupplier s/Bool
   (s/optional-key :DefaultCurrency) s/Str
   (s/optional-key :EmailAddress) s/Str
   :UpdatedDateUTC s/Str
   :HasAttachments s/Bool
   :ContactStatus s/Str
   :ContactID s/Str
   :Phones [{:PhoneType s/Str,
             (s/optional-key :PhoneAreaCode) s/Str
             (s/optional-key :PhoneCountryCode) s/Str
             (s/optional-key :PhoneNumber) s/Str}]
   (s/optional-key :FirstName) s/Str
   :Addresses [{(s/optional-key :AddressLine2) s/Str
                (s/optional-key :Country) s/Str
                (s/optional-key :AttentionTo) s/Str
                (s/optional-key :AddressLine4) s/Str
                (s/optional-key :PostalCode) s/Str
                (s/optional-key :Region) s/Str
                (s/optional-key :City) s/Str
                (s/optional-key :AddressLine1) s/Str
                (s/optional-key :AddressLine3) s/Str
                :AddressType s/Str}]
   (s/optional-key :AccountNumber) s/Str
   :ContactPersons [s/Any]
   (s/optional-key :LastName) s/Str
   :ContactGroups [s/Any]
   (s/optional-key :BankAccountDetails) s/Str
   (s/optional-key :Balances) {:AccountsPayable {:Overdue s/Num
                                                 :Outstanding s/Num}
                               :AccountsReceivable {:Overdue s/Num
                                                    :Outstanding s/Num}}
   :HasValidationErrors s/Bool
   :Name s/Str
   :IsCustomer s/Bool
   (s/optional-key :Website) s/Str})

